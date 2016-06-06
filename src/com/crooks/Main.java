package com.crooks;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static User user;

    public static void main(String[] args) {
	Spark.init();
    Spark.get(          //Spark.get() at a minimum requires 2 arguments, The name of the route and the function to run
            "/",                          //Name of the route
            (request, response)-> {       //This line will be the function that runs when the user lands on this;
                HashMap m = new HashMap();
                if (user== null){           // if the user hasn't logged in direct to login page, else return hello world
                    return new ModelAndView(m,"login.html");

                }else {
                    m.put("name", user.name);                 //Using user.name allows us to use the name the person enters rather than the hardcoded one from earlier
                    return new ModelAndView(m, "home.html");  //this uses the mustacheTemplate Engine, and allows injection of content into the html using the mustache tag {{content}}
                }
            },
            new MustacheTemplateEngine()  //To pull from a template a third argument is used, but now the lambda must  be altered
    );
    Spark.post(         //Post routes don't return html so will different from Get Routes; Posts do some work and redirect you to a get route.
            "/login",                   //Name of the route
            ((request, response) -> {   //Function that runs...
                String username = request.queryParams("username");  //this is how you call the information the user is passing to the server
                user = new User(username);                          // Perform some action with the information
                response.redirect("/");                             // redirect to "/"
                return "";                                          // required to return something but since were redirecting a blank is sufficient
            }
    ));

    Spark.post(
            "/logout",
            (request, response) -> {
                user = null;
                response.redirect("/");
                return "";
            }
    );



    }
}
