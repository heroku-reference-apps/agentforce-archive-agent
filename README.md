Archive Agent for use with Agentforce
-------------------------------------

Extends Salesforce [Agentforce](https://www.salesforce.com/agentforce/) with a custom action that processes unstructured data with AI. This example is covered in more detail within this [blog](https://blog.heroku.com/building-supercharged-agents-heroku-agentforce) and [video](https://youtu.be/mNgrdf1GX-w). Also checkout our other Agentforce demo and sample code [here](https://github.com/heroku-reference-apps/agentforce-archive-agent/tree/main?tab=readme-ov-file#want-more).

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

![Diagram](/images/diagram.jpg)

Heroku applications can supercharge [Agentforce](https://www.salesforce.com/agentforce/) because they:
- Can process unstructured data in variety of ways in real-time
- Can vertically scale to use large in memory data structures to support analytics of that data on the fly 
- Can leverage AI LLM to generate data queries from natural language prompts 
- Can leverage common libraries such as Java and Spring that have purpose built components
- Can leverage different programming languages, such as Python often used when building AI applications.

This demo includes:
- Java and [Spring Boot](https://spring.io/projects/spring-boot) to build a Agentforce action
- Archive data (``/invoices``) is ingested into an in-memory database (H2) (optional persisted DB)
- [Spring AI](https://spring.io/projects/spring-ai) takes the natural language Agentforce prompt and converts it to SQL via [ChatGPT](https://chat.openai.com/)
- The AI generated SQL query is run and the query result is returned in the action response to Agentforce 
- The library [org.springdoc](https://springdoc.org/) is used to automatically generate OpenAPI schema and Swagger UI interfaces

These are some of the prompts it can handle:
- *Which product invoiced had the most units sold?*
- *What was the total invoiced amount on Planning Meetings?*
- *From the invoices what were the total units sold for Consulting Hours?*
- *Total invoiced amount in 2009?*
- *Can you list the invoice numbers for 2008?*
- *Can you list the products included in the invoices in 2008?*
- *What was the total invoiced amount on 'Planning Meetings' OR TOTALPRICE = 0?*

Basic Authentication Configuration
--------------------

Its good practice for any API that exposes information to have authentication. Java Spring (the library used in this example) makes it very easy to configure a number of security approaches. To keep things simple for this example HTTP Basic authentication is enabled with a hard coded user ``heroku`` and password ``agent``. 

**IMPORTANT:** Please do change this authentication configuration before deploying to production. Typically JWT authentication is used for APIs.

Running Locally
---------------

Use the following command to run locally:

```./mvnw spring-boot:run```

To test the API use the
[Swagger UI](http://localhost:8080/swagger-ui/index.html#/query-controller/processQuery) with user, ``heroku`` and password ``agent``. Make sure to provide a valid ``OPENAI_API_KEY``. This demo highlights that Heroku applications are ideal candidates to host [Agentforce](https://www.salesforce.com/agentforce/) actions that require complex intensive compute.

Deploy
------

You will need to deploy this API before you can connect it to Agentforce. Heroku makes this very easy. First create a Heroku app to deploy this application by running:

```heroku create <app-name>```

Use the following command to configure your ```OPENAI_API_KEY```

```heroku config:set OPENAI_API_KEY=<value>```

Finally deploy the API to Heroku.

```git push heroku main```

More detailed instructions for how to deploy using Heroku Git can be found [here](https://devcenter.heroku.com/articles/git#create-a-heroku-remote).

Configure Agentforce
--------------------

Complete this [tutorial](https://github.com/heroku-examples/heroku-agentforce-tutorial?tab=readme-ov-file#creating-agentforce-custom-actions-with-heroku) to learn how configure this action within your org.

Want more?
------------
Good news! Since publishing this demo, we’ve also released an additional [demo video](https://www.youtube.com/watch?v=yd97A9GLFUA&t=2s) and [sample code](https://github.com/heroku-examples/agentforce-collage-agent), diving deeper into how Heroku enhances Agentforce agents' capabilities. In this expanded version of the popular [Coral Cloud Resort demo](https://trailhead.salesforce.com/content/learn/projects/quick-start-explore-the-coral-cloud-sample-app), vacationing guests can use Agentforce to browse and book unique experiences. With Heroku, the agent can even generate personalized adventure collages for each guest, showcasing how custom code on Heroku enables dynamic digital media creation directly within the Agentforce experience.
