package com.crooks;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static User user;
    static HashMap<String, User> users = new HashMap<>();
    static ArrayList<User> userList = new ArrayList<User>();


    public static void main(String[] args) {
	Spark.init();

        Spark.get(                                                    //Spark.get() at a minimum requires 2 arguments, The name of the route and the function to run
            "/",                                                      //Name of the route
            (request, response)-> {                                   //This line will be the function that runs when the user lands on this;
                Session session = request.session();
                String username = session.attribute("username");      // This session call will pull  existing session if it exists, it will pull null if there is nothing there

                HashMap m = new HashMap();
                if (username== null){                                     // if the user hasn't logged in direct to login page, else return hello world
                    return new ModelAndView(m,"login.html");

                }else {
                    m.put("name", username);                        //Using user.name allows us to use the name the person enters rather than the hardcoded one from earlier
                    m.put("users", userList);                        //passing the User list through
                    return new ModelAndView(m, "home.html");         //this uses the mustacheTemplate Engine, and allows injection of content into the html using the mustache tag {{content}}
                }
            },
            new MustacheTemplateEngine()  //To pull from a template a third argument is used, but now the lambda must  be altered
        );

        Spark.post(                                  //Post routes don't return html so will different from Get Routes; Posts do some work and redirect you to a get route.
            "/login",                                //Name of the route
            ((request, response) -> {                //Function that runs...
                String username = request.queryParams("username");  //this is how you call the information the user is passing to the server
                User user = users.get(username);
                if (user==null) {                                   //Check to see if user already exists
                    user = new User(username);                      // Perform some action with the information
                    users.put(username, user);                      //Create User Hash Key-Value pair
                    userList.add(user);                             // Adds User to the User list
                }
                Session session = request.session();            //Creating a new session
                session.attribute("username", username);        //acts like a HashMap.put() that holds any key pair value that you want to remember about this user


                response.redirect("/");                             // redirect to "/"
                return "";                                          // required to return something but since were redirecting a blank is sufficient
            }
        ));

//just doing this so i can re-commit
        Spark.post(
            "/logout",
            (request, response) -> {
                Session session = request.session();
                session.invalidate();                   //When logging out you want to invalidate the session


                response.redirect("/");
                return "";
            }
         );
    }
}
