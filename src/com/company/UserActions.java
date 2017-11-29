package com.company;

/**
 * Just a simple helper for {@link com.company.SimpleUserActionsCounter}
 */
public class UserActions {
    private final String type;
    private final String user;
    UserActions(String type, String user) {
        this.type = type;
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }
    private String getType() {
        return this.type;
    }

    // here is where the Collenctions.frequency will compare the objects
    @Override
    public boolean equals(Object o){
        UserActions newUser;
        if(!(o instanceof UserActions)){
            return false;
        }else{
            newUser = (UserActions) o;
            if(this.user.equals(newUser.getUser()) && this.type.equals(newUser.getType())){
                return true;
            }
        }
        return false;
    }
}
