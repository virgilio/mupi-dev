package security;

import be.objectify.deadbolt.AbstractDynamicResourceHandler;
import be.objectify.deadbolt.DeadboltHandler;
import be.objectify.deadbolt.DynamicResourceHandler;
import be.objectify.deadbolt.models.Permission;
import be.objectify.deadbolt.models.RoleHolder;
import play.Logger;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import controllers.Mupi;

public class MyDynamicResourceHandler implements DynamicResourceHandler{
    private static final Map<String, DynamicResourceHandler> HANDLERS = new HashMap<String, DynamicResourceHandler>();

    static{
      HANDLERS.put("editor",
          new AbstractDynamicResourceHandler(){
            public boolean isAllowed(String name,String meta, DeadboltHandler deadboltHandler,Http.Context context){
              models.User u = Mupi.getLocalUser(context.session());
              return u.status == 1;
            }
        }
      );
    }
    
    public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context){
        DynamicResourceHandler handler = HANDLERS.get(name);
        boolean result = false;
        if (handler == null){
            Logger.error("No handler available for " + name);
        }else{
            result = handler.isAllowed(name, meta, deadboltHandler, context);
        }
        return result;
    }

    public boolean checkPermission(String permissionValue, DeadboltHandler deadboltHandler, Http.Context ctx){
        boolean permissionOk = false;
        RoleHolder roleHolder = deadboltHandler.getRoleHolder(ctx);
        
        if (roleHolder != null){
            List<? extends Permission> permissions = roleHolder.getPermissions();
            for (Iterator<? extends Permission> iterator = permissions.iterator(); !permissionOk && iterator.hasNext(); ){
                Permission permission = iterator.next();
                permissionOk = permission.getValue().contains(permissionValue);
            }
        }        
        return permissionOk;
    }
}