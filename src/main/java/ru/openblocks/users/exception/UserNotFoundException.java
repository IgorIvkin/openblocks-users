package ru.openblocks.users.exception;

public class UserNotFoundException extends RuntimeException {

   public UserNotFoundException() {
       super();
   }

   public UserNotFoundException(String message) {
       super(message);
   }

   public static UserNotFoundException ofUserId(Long userId) {
       return new UserNotFoundException("Cannot find user by ID " + userId);
   }

    public static UserNotFoundException ofUserName(String userName) {
        return new UserNotFoundException("Cannot find user by username " + userName);
    }
}
