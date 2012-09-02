package models;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import models.TokenAction.Type;
import play.data.format.Formats;
import play.db.ebean.Model;
import scala.actors.threadpool.Arrays;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "profiles")
public class Profile extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	public User user;
	public String firsName;
	public String lastName;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date birthDate;
	public String picture;
	public String about;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;


	public static final Finder<Long, Profile> find = new Finder<Long, Profile>(
			Long.class, Profile.class);


	/**	
	 * Método que cria profile. 
	 * @param authUser
	 * @return
	 */
	public static Profile create(final AuthUser authUser) {
		final Profile p = new Profile();
		p.user = new User();
		p.user.roles = Collections.singletonList(SecurityRole
				.findByRoleName(controllers.Application.USER_ROLE));
		// user.permissions = new ArrayList<UserPermission>();
		// user.permissions.add(UserPermission.findByValue("printers.edit"));
		p.user.active = true;
		p.user.lastLogin = new Date();
		p.user.linkedAccounts = Collections.singletonList(LinkedAccount
				.create(authUser));

		p.user.created = new Date();
		p.user.modified = new Date();

		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			p.user.email = identity.getEmail();
			p.user.emailValidated = false;
		}

		if (authUser instanceof NameIdentity) {
			final NameIdentity identity = (NameIdentity) authUser;
			final String name = identity.getName();
			if (name != null) {
				p.user.name = name;
			}
		}

		p.user.save();
		p.user.saveManyToManyAssociations("roles");
		// user.saveManyToManyAssociations("permissions");
		return p;
	}



	public static boolean existsByAuthUserIdentity(
			final AuthUserIdentity identity) {
		final ExpressionList<Profile> exp;
		if (identity instanceof UsernamePasswordAuthUser) {
			exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
		} else {
			exp = getAuthUserFind(identity);
		}
		return exp.findRowCount() > 0;
	}

	private static ExpressionList<Profile> getAuthUserFind(
			final AuthUserIdentity identity) {
		return find.where().eq("active", true)
				.eq("linkedAccounts.providerUserId", identity.getId())
				.eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public static Profile findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		if (identity instanceof UsernamePasswordAuthUser) {
			return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
		} else {
			return getAuthUserFind(identity).findUnique();
		}
	}

	public static Profile findByUsernamePasswordIdentity(
			final UsernamePasswordAuthUser identity) {
		return getUsernamePasswordAuthUserFind(identity).findUnique();
	}

	private static ExpressionList<Profile> getUsernamePasswordAuthUserFind(
			final UsernamePasswordAuthUser identity) {
		return getEmailUserFind(identity.getEmail()).eq(
				"linkedAccounts.providerKey", identity.getProvider());
	}

	public void merge(final Profile otherUser) {
		for (final LinkedAccount acc : otherUser.user.linkedAccounts) {
			this.user.linkedAccounts.add(LinkedAccount.create(acc));
		}
		// do all other merging stuff here - like resources, etc.

		// deactivate the merged user that got added to this one
		otherUser.user.active = false;
		Ebean.save(Arrays.asList(new Profile[] { otherUser, this }));
	}



	public static void merge(final AuthUser oldUser, final AuthUser newUser) {
		Profile.findByAuthUserIdentity(oldUser).merge(
				Profile.findByAuthUserIdentity(newUser));
	}

	public Set<String> getProviders() {
		final Set<String> providerKeys = new HashSet<String>(
				this.user.linkedAccounts.size());
		for (final LinkedAccount acc : this.user.linkedAccounts) {
			providerKeys.add(acc.providerKey);
		}
		return providerKeys;
	}

	public static void addLinkedAccount(final AuthUser oldUser,
			final AuthUser newUser) {
		final Profile u = Profile.findByAuthUserIdentity(oldUser);
		u.user.linkedAccounts.add(LinkedAccount.create(newUser));
		u.save();
	}

	public static void setLastLoginDate(final AuthUser knownUser) {
		final Profile u = Profile.findByAuthUserIdentity(knownUser);
		u.user.lastLogin = new Date();
		u.save();
	}

	public static void setLastModified(final AuthUser knownUser) {
		final Profile u = Profile.findByAuthUserIdentity(knownUser);
		u.modified = new Date();
		u.save();
	}

	public static Profile findByEmail(final String email) {
		return getEmailUserFind(email).findUnique();
	}

	private static ExpressionList<Profile> getEmailUserFind(final String email) {
		return find.where().eq("active", true).eq("email", email);
	}

	public LinkedAccount getAccountByProvider(final String providerKey) {
		return LinkedAccount.findByProviderKey(this.user, providerKey);
	}

	public static void verify(final Profile unverified) {
		// You might want to wrap this into a transaction
		unverified.user.emailValidated = true;
		unverified.save();
		TokenAction.deleteByUser(unverified.user, Type.EMAIL_VERIFICATION);
	}

	public void changePassword(final UsernamePasswordAuthUser authUser,
			final boolean create) {
		LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
		if (a == null) {
			if (create) {
				a = LinkedAccount.create(authUser);
				a.user = this.user;
			} else {
				throw new RuntimeException(
						"Account not enabled for password usage");
			}
		}
		a.providerUserId = authUser.getHashedPassword();
		a.save();
	}

	public void resetPassword(final UsernamePasswordAuthUser authUser,
			final boolean create) {
		// You might want to wrap this into a transaction
		this.changePassword(authUser, create);
		TokenAction.deleteByUser(this.user, Type.PASSWORD_RESET);
	}
}
