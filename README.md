Archive Agent for use with Agentforce
-------------------------------------

Extends Salesforce [Agentforce](https://www.salesforce.com/agentforce/) with a custom action that processes unstructured data with AI. This example is covered in more detail within this [blog](https://blog.heroku.com/) and [video](https://youtu.be/mNgrdf1GX-w).

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

![Diagram](/images/diagram.jpg)

Heroku applications can supercharge [Agentforce](https://www.salesforce.com/agentforce/) because they:
- Can process unstructured data in variety of ways in realtime
- Can vertically scale to use large in memory data structures to support analytics of that data on the fly 
- Can leverage AI LLM to generate data queries from natural language prompts 
- Can leverage common libraries such as Java and Spring that have purpose built components
- Can leverage different programming languages, such as Python often used when building AI applciaitons.

This demo includes:
- Java and [Spring Boot](https://spring.io/projects/spring-boot) to build a Agentforce action
- Archive data (``/invoices``) is ingested into an in-memory database (H2) (optional persisted DB)
- [Spring AI](https://spring.io/projects/spring-ai) takes the natural language Agentforce prompt and converts it to SQL via [ChatGPT](https://chat.openai.com/)
- The AI generated SQL query is run and the query result is returned in the action response to Agentforce 
- The library [org.springdoc](https://springdoc.org/) is used to automatically generate OpenAPI schema and Swagger UI interfaces

These are some of the prompts it can handle:
- *Which product invoiced had the most units sold?*
- *What was the total invoiced amount on Planning Meetings?*
- *From the invoices what where the total units sold for Consulting Hours?*
- *Total invoiced amount in 2009?*
- *Can you list the invoice numbers for 2008?*
- *Can you list the products included in the invoices in 2008?*
- *What was the total invoiced amount on 'Planning Meetings' OR TOTALPRICE = 0?*

Basic Authentication Configuration
--------------------

Its good practice for any API that exposes information to have authentication. Java Spring (the library used in this example) makes it very easy to configure a number of security approaches. To keep things simple for this example HTTP Basic authentication is enabled with a hard coded user ``heroku`` and password ``agent``. 

**IMPORTANT:** Please do change this authentiation configuration before deploying to production. Typically JWT authentication is used for APIs.

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

Knowing the API URL and OpenAPI Spec
-----------------------------

Once deployed, make sure you know the URL for your deployed API (Heroku app), including the path to your OpenAPI schema. The app URL is output after the deploy completes and can also be obtained by using the command ``heroku open``. The OpenAPI schema will be available on this URL path ``/v3/api-docs``.

Adding to Agentforce
--------------------

These steps assume you have knowledge of **External Services**, **Permission Sets**, **Flow** and **Agent Builder**. If you are not familar with these features, a future iteration of this example will include example metadata for the above steps that you can use to deploy using SFDX. 

To create a custom agent action in Agent Builder you must first import the API using External Services and then create a simple Flow wrapper for your action. At the time of publishing this example support for External Service actions within Agent Builder had not been released, in the future the Flow wrapper will not be required.

### Step 1. Create a External Credential

This example use HTTP Basic authentication, follow [these](https://help.salesforce.com/s/articleView?id=sf.nc_create_edit_basic_auth_ext_cred.htm&type=5) steps to set this up, using the user name and password above. Before moving on do not forget to create a Permission Set that exposes the principle you created in this step and then assign it to your user.

### Step 2. Create a External Service

Copy paste your OpenAPI schema into your clipboard and follow [these](https://help.salesforce.com/s/articleView?id=sf.external_services_register_json.htm&type=5) steps to import your API, using the External/Name Credential you created above when prompted.

### Step 3. Create a Flow Action

Create an Autolaunch Flow that calls the External Service action created by the platform as part of the previous step. The Flow should make public two properties a ``Query (string)`` and a ``QueryResult (string)`` and use the Assignment action in the Flow to copy the values from and to these prioperites before and after calling the the API. This [help topic](https://help.salesforce.com/s/articleView?id=sf.voice_conversation_intelligence_autolaunched_flow_setup.htm&type=5) describes how to create a basic auto launched flow that will be accessible from Agent Builder.

### Step 4. Create a Agent Action

Create a Agent action that leverages the Flow action created previously using [these](https://help.salesforce.com/s/articleView?id=sf.copilot_actions_custom_create_scratch.htm&type=5) steps. Here is an example of the type instructions to enter.
- For **Agent Action Instructions** enter _Ability to extract, query and calculate information on historic invoice information_.
- For **Query Instructions** enter _This is the query the user is requesting against the invoice information pass it directly through. Pass this through to the action directly and do not try to generate a SQL query from it_.
- For **Query Result** enter _This is the response in JSON format, extract the value and display accordingly based on the query the user made_.

### Step 5. Adding Agent Action to an Agent 

Before you can add your action to an agent (such as Einstein Copilot) you must add it to a topic. It is recommended you create a new topic for this action. The steps are described [here](https://help.salesforce.com/s/articleView?id=sf.copilot_actions_add.htm&type=5).
- For **Topic Label** enter _Query Historic Archived Invoices_.
- For **Classification Description** enter _Actions in this topic have the ability to access historic archived information, such as invoices. Users may ask about invoice amounts and products included in the invoices_.
- For **Scope** enter _Your job is to provide information about historic invoices, such as products and invoice amounts_.
- For **Instructions** enter _Pass the query unmodified directly to the action_.
- For **Instrucitons** enter _There is only one action in this topic please use it_.

### Testing

You can now enter into the Agent or Agent Builder prompt some of the prompts shown at the top of this README. If you see any issues, try running the prompt from within the Agent Builder first and also check the Heroku logs for your API app.