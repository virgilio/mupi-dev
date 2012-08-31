//package models;
//
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.ManyToMany;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//
//import models.TokenAction.Type;
//
//import play.data.format.Formats;
//import play.db.ebean.Model;
//import scala.actors.threadpool.Arrays;
//import be.objectify.deadbolt.models.Permission;
//import be.objectify.deadbolt.models.Role;
//import be.objectify.deadbolt.models.RoleHolder;
//
//import com.avaje.ebean.Ebean;
//import com.avaje.ebean.ExpressionList;
//import com.avaje.ebean.validation.Email;
//import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
//import com.feth.play.module.pa.user.AuthUser;
//import com.feth.play.module.pa.user.AuthUserIdentity;
//import com.feth.play.module.pa.user.EmailIdentity;
//import com.feth.play.module.pa.user.NameIdentity;
//
///**
// * Initial version based on work by Steve Chaloner (steve@objectify.be) for
// * Deadbolt2
// */
//@Entity
//@Table(name = "profiles")
//public class Profile extends Model {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	@Id
//	public User user;
//
//	first_name
//	last_name
//	birth_date
//	picture
//	about
//	created
//	modified
//	
//	
//	
//	@Email
//	// if you make this unique, keep in mind that users *must* merge/link their
//	// accounts then on signup with additional providers
//	// @Column(unique = true)
//	public String email;
//
//	public String name;
//
//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
//	public Date lastLogin;
//
//	public boolean active;
//
//	public boolean emailValidated;
//	
//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
//	public Date created;
//	
//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
//	public Date lastModified;
//
//	@ManyToMany
//	public List<SecurityRole> roles;
//
//	@OneToMany(cascade = CascadeType.ALL)
//	public List<LinkedAccount> linkedAccounts;
//
//	@ManyToMany
//	public List<UserPermission> permissions;
//
//	public static final Finder<Long, Profile> find = new Finder<Long, Profile>(
//			Long.class, Profile.class);
//
//	@Override
//	public List<? extends Role> getRoles() {
//		return roles;
//	}
//
//	@Override
//	public List<? extends Permission> getPermissions() {
//		return permissions;
//	}
//
//	public static boolean existsByAuthUserIdentity(
//			final AuthUserIdentity identity) {
//		final ExpressionList<Profile> exp;
//		if (identity instanceof UsernamePasswordAuthUser) {
//			exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
//		} else {
//			exp = getAuthUserFind(identity);
//		}
//		return exp.findRowCount() > 0;
//	}
//
//	private static ExpressionList<Profile> getAuthUserFind(
//			final AuthUserIdentity identity) {
//		return find.where().eq("active", true)
//				.eq("linkedAccounts.providerUserId", identity.getId())
//				.eq("linkedAccounts.providerKey", identity.getProvider());
//	}
//
//	public static Profile findByAuthUserIdentity(final AuthUserIdentity identity) {
//		if (identity == null) {
//			return null;
//		}
//		if (identity instanceof UsernamePasswordAuthUser) {
//			return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
//		} else {
//			return getAuthUserFind(identity).findUnique();
//		}
//	}
//
//	public static Profile findByUsernamePasswordIdentity(
//			final UsernamePasswordAuthUser identity) {
//		return getUsernamePasswordAuthUserFind(identity).findUnique();
//	}
//
//	private static ExpressionList<Profile> getUsernamePasswordAuthUserFind(
//			final UsernamePasswordAuthUser identity) {
//		return getEmailUserFind(identity.getEmail()).eq(
//				"linkedAccounts.providerKey", identity.getProvider());
//	}
//
//	public void merge(final Profile otherUser) {
//		for (final LinkedAccount acc : otherUser.linkedAccounts) {
//			this.linkedAccounts.add(LinkedAccount.create(acc));
//		}
//		// do all other merging stuff here - like resources, etc.
//
//		// deactivate the merged user that got added to this one
//		otherUser.active = false;
//		Ebean.save(Arrays.asList(new Profile[] { otherUser, this }));
//	}
//
//	public static Profile create(final AuthUser authUser) {
//		final Profile user = new Profile();
//		user.roles = Collections.singletonList(SecurityRole
//				.findByRoleName(controllers.Application.USER_ROLE));
//		// user.permissions = new ArrayList<UserPermission>();
//		// user.permissions.add(UserPermission.findByValue("printers.edit"));
//		user.active = true;
//		user.lastLogin = new Date();
//		user.linkedAccounts = Collections.singletonList(LinkedAccount
//				.create(authUser));
//		
//		user.created = new Date();
//		user.lastModified = new Date();
//
//		if (authUser instanceof EmailIdentity) {
//			final EmailIdentity identity = (EmailIdentity) authUser;
//			// Remember, even when getting them from FB & Co., emails should be
//			// verified within the application as a security breach there might
//			// break your security as well!
//			user.email = identity.getEmail();
//			user.emailValidated = false;
//		}
//
//		if (authUser instanceof NameIdentity) {
//			final NameIdentity identity = (NameIdentity) authUser;
//			final String name = identity.getName();
//			if (name != null) {
//				user.name = name;
//			}
//		}
//
//		user.save();
//		user.saveManyToManyAssociations("roles");
//		// user.saveManyToManyAssociations("permissions");
//		return user;
//	}
//
//	public static void merge(final AuthUser oldUser, final AuthUser newUser) {
//		Profile.findByAuthUserIdentity(oldUser).merge(
//				Profile.findByAuthUserIdentity(newUser));
//	}
//
//	public Set<String> getProviders() {
//		final Set<String> providerKeys = new HashSet<String>(
//				linkedAccounts.size());
//		for (final LinkedAccount acc : linkedAccounts) {
//			providerKeys.add(acc.providerKey);
//		}
//		return providerKeys;
//	}
//
//	public static void addLinkedAccount(final AuthUser oldUser,
//			final AuthUser newUser) {
//		final Profile u = Profile.findByAuthUserIdentity(oldUser);
//		u.linkedAccounts.add(LinkedAccount.create(newUser));
//		u.save();
//	}
//
//	public static void setLastLoginDate(final AuthUser knownUser) {
//		final Profile u = Profile.findByAuthUserIdentity(knownUser);
//		u.lastLogin = new Date();
//		u.save();
//	}
//	
//	public static void setLastModified(final AuthUser knownUser) {
//		final Profile u = Profile.findByAuthUserIdentity(knownUser);
//		u.lastModified = new Date();
//		u.save();
//	}
//
//	public static Profile findByEmail(final String email) {
//		return getEmailUserFind(email).findUnique();
//	}
//
//	private static ExpressionList<Profile> getEmailUserFind(final String email) {
//		return find.where().eq("active", true).eq("email", email);
//	}
//
//	public LinkedAccount getAccountByProvider(final String providerKey) {
//		return LinkedAccount.findByProviderKey(this, providerKey);
//	}
//
//	public static void verify(final Profile unverified) {
//		// You might want to wrap this into a transaction
//		unverified.emailValidated = true;
//		unverified.save();
//		TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
//	}
//
//	public void changePassword(final UsernamePasswordAuthUser authUser,
//			final boolean create) {
//		LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
//		if (a == null) {
//			if (create) {
//				a = LinkedAccount.create(authUser);
//				a.user = this;
//			} else {
//				throw new RuntimeException(
//						"Account not enabled for password usage");
//			}
//		}
//		a.providerUserId = authUser.getHashedPassword();
//		a.save();
//	}
//
//	public void resetPassword(final UsernamePasswordAuthUser authUser,
//			final boolean create) {
//		// You might want to wrap this into a transaction
//		this.changePassword(authUser, create);
//		TokenAction.deleteByUser(this, Type.PASSWORD_RESET);
//	}
//}
