package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import models.TokenAction.Type;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import scala.actors.threadpool.Arrays;
import be.objectify.deadbolt.models.Permission;
import be.objectify.deadbolt.models.Role;
import be.objectify.deadbolt.models.RoleHolder;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.validation.Email;
import com.feth.play.module.pa.providers.oauth2.facebook.FacebookAuthUser;
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
@Table(name = "users")
public class User extends Model implements RoleHolder {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	@Email
	// if you make this unique, keep in mind that users *must* merge/link their
	// accounts then on signup with additional providers
	// @Column(unique = true)
	public String email;


	public String name;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date lastLogin;

	public boolean active;

	public int status;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;

	@ManyToMany
	public List<SecurityRole> roles;

	@OneToMany(cascade = CascadeType.ALL)
	public List<LinkedAccount> linkedAccounts;

	@ManyToMany
	public List<UserPermission> permissions;

	@ManyToMany(cascade = CascadeType.ALL)
  private List<Promotion> subscriptions = new ArrayList<Promotion>();

	@OneToOne(cascade = CascadeType.ALL)
	public Profile profile;


	public static final Finder<Long, User> find = new Finder<Long, User>(
			Long.class, User.class);

	@Override
	public List<? extends Role> getRoles() {
		return roles;
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return permissions;
	}

	public Profile getProfile(){
		return this.profile;
	}

	public static boolean existsByAuthUserIdentity(final AuthUserIdentity identity) {
		final ExpressionList<User> exp;
		if (identity instanceof UsernamePasswordAuthUser) {
			exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
		} else {
			exp = getAuthUserFind(identity);
			
		}
		return exp.findRowCount() > 0;
	}

	private static ExpressionList<User> getAuthUserFind(final AuthUserIdentity identity) {

		return find.where().eq("active", true)
				.eq("linkedAccounts.providerUserId", identity.getId())
				.eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		if (identity instanceof UsernamePasswordAuthUser) {
			return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
		} else {
			return getAuthUserFind(identity).findUnique();
		}
	}

	public static User findByUsernamePasswordIdentity(
			final UsernamePasswordAuthUser identity) {
		return getUsernamePasswordAuthUserFind(identity).findUnique();
	}

	private static ExpressionList<User> getUsernamePasswordAuthUserFind(final UsernamePasswordAuthUser identity) {
	  //TODO:  Is this important? .eq("linkedAccounts.providerKey", identity.getProvider());
		return getEmailUserFind(identity.getEmail()).eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public void merge(final User otherUser) {
		for (final LinkedAccount acc : otherUser.linkedAccounts) {
			this.linkedAccounts.add(LinkedAccount.create(acc));
		}
		// do all other merging stuff here - like resources, etc.

		// deactivate the merged user that got added to this one
		otherUser.active = false;
		Ebean.save(Arrays.asList(new User[] { otherUser, this }));
	}

	public static User updateName(final User user, final String name) {
		final User u = findByEmail(user.email);
		u.name = name;
		u.modified = new Date();
		u.update();
		return u;
	}


	public static User create(final AuthUser authUser) {
		final User user = new User();
		user.roles = Collections.singletonList(SecurityRole
				.findByRoleName(controllers.Mupi.USER_ROLE));
		// user.permissions = new ArrayList<UserPermission>();
		// user.permissions.add(UserPermission.findByValue("printers.edit"));
		user.active = true;
		user.lastLogin = new Date();
		user.linkedAccounts = Collections.singletonList(LinkedAccount.create(authUser));

		
		user.created = new Date();
		user.modified = new Date();
		if (authUser instanceof EmailIdentity) {
		  final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			user.email = identity.getEmail();
			user.status = 0;
			user.profile = new Profile();
		}
		
		
		if (authUser instanceof NameIdentity) {
	
		  if( authUser instanceof FacebookAuthUser ){
	      final FacebookAuthUser identity = (FacebookAuthUser) authUser;
	      user.email = identity.getEmail();
	      user.status = (identity.isVerified() ? 1 : 0);
	      user.name = identity.getFirstName();
	      user.profile = new Profile(
	          user,
	          identity.getFirstName(),
	          identity.getLastName(),
	          null,
	          null,
	          "",
	          (identity.getGender().trim().compareToIgnoreCase("female") == 0) ? 1 : (identity.getGender().trim().compareToIgnoreCase("male") == 0) ? 2 : 3,
	          2,
	          null
	      );
	    }
		  else{
  			final NameIdentity identity = (NameIdentity) authUser;
  			final String name = identity.getName();
  			if (name != null) {
  				user.name = name;
  				user.profile = new Profile(name);
  			}
  			else{
  			  user.profile = new Profile();
  			}
		  }
		}
		

		user.save();

		user.saveManyToManyAssociations("roles");
		// user.saveManyToManyAssociations("permissions");
		return user;
	}

	public static void merge(final AuthUser oldUser, final AuthUser newUser) {
		User.findByAuthUserIdentity(oldUser).merge(
				User.findByAuthUserIdentity(newUser));
	}

	public Set<String> getProviders() {
		final Set<String> providerKeys = new HashSet<String>(
				linkedAccounts.size());
		for (final LinkedAccount acc : linkedAccounts) {
			providerKeys.add(acc.providerKey);
		}
		return providerKeys;
	}

	public static void addLinkedAccount(final AuthUser oldUser,
			final AuthUser newUser) {
		final User u = User.findByAuthUserIdentity(oldUser);
		u.linkedAccounts.add(LinkedAccount.create(newUser));
		u.save();
	}

	public static void setLastLoginDate(final AuthUser knownUser) {
		final User u = User.findByAuthUserIdentity(knownUser);
		u.lastLogin = new Date();
		u.save();
	}

	public static void setLastModified(final AuthUser knownUser) {
		final User u = User.findByAuthUserIdentity(knownUser);
		u.modified = new Date();
		u.save();
	}

	public static User findByEmail(final String email) {
	  User u = getEmailUserFind(email).findUnique();
    return u;
	}

	private static ExpressionList<User> getEmailUserFind(final String email) {
	  ExpressionList<User> u = find.where().eq("active", true).eq("email", email);
		return u;
	}

	public LinkedAccount getAccountByProvider(final String providerKey) {
		return LinkedAccount.findByProviderKey(this, providerKey);
	}

	public static void verify(final User unverified) {
		// You might want to wrap this into a transaction
		unverified.status = 1;
		unverified.save();
		TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
	}

	public void changePassword(final UsernamePasswordAuthUser authUser,
			final boolean create) {
		LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
		if (a == null) {
			if (create) {
				a = LinkedAccount.create(authUser);
				a.user = this;
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
		TokenAction.deleteByUser(this, Type.PASSWORD_RESET);
	}

  public Date getCreated() {
    return created;
  }

  public String getCohortString() {
//    Y: Year (4 characters)
//    Q: Quarter (1 character, 1 – 4)
//    M: Month (2 characters, 01-12)
//    WY: Week Year (4 characters)
//    WM: Week Month (2 characters, 01-12)
//    WD: Week Day (2 characters, 01-31)
//    D: Day (2 characters, 01-31)
//    H: Hour (2 characters, 00-23)

    Calendar cal = Calendar.getInstance();
    cal.setTime(this.getCreated());

    return "Y:"  + cal.get(Calendar.YEAR) + ";"
    		 + "Q:"  + cal.get(Calendar.MONTH % 4) + ";"
    		 + "M:"  + cal.get(Calendar.MONTH) + ";"
    		 + "WY:" + cal.get(Calendar.WEEK_OF_YEAR) + ";"
    		 + "WM:" + cal.get(Calendar.WEEK_OF_MONTH) + ";"
    		 + "WD:" + cal.get(Calendar.DAY_OF_WEEK) + ";"
    		 + "D:"  + cal.get(Calendar.DAY_OF_MONTH) + ";"
   	  	 + "H:"  + cal.get(Calendar.HOUR_OF_DAY) + ";"
   		  ;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


}
