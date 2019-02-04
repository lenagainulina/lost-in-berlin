## Lost-in-Berlin

 A B2C web and mobile app.

## In short
App offers a quick B2C mediation in a touristic branch. Same model can be later expanded on other service branches. 

## Main feature for the client 
>  Click a button, get an offer. 

Enter a basic description of your order (date, time and number of participants), your contact information (phone and/or e-mail) and choose businesses you want to get offers from.  

## Main feature for the business 
>  Be the first one to take an order. 

See all orders and their status (active/on hold) in a list. Be the first one to click on active orders to get client’s contact information.

## Workflow 
### Client
1. Search 
* Sets location manually or accepts automatically detected location
* Chooses businesses from a gallery in a tinder-like fashion (swipes through the profiles and marks his choice with +/-).
* Clicks the button "Proceed to order" to move to the page with order details.

2. Order
* Fills out contact information fields (Name, e-mail, phone number), which is required, and marks the prefered way of communication, which is optional. Fills out fields with order details (date, time, number of participants, additional information), which is also optional.
* Clicks the button "Post my order" to publish the service request among the businesses that were chosen before. Order form is passed to the server, where it is saved and filtered by city.
* Order status appears on a status bar of the order form: pending (no business has replied)/ accepted by [business name]. Additionally, client receives an e-mail: “Your order has been accepted by [Name of the business]. You will get contacted in 10-15 minutes.”
* Once the deal is made, client clicks the button “close my order”. The order form dissappears, client is thrown back to the search page. 
* To continue search with the rest of the businesses chosen before, client clicks the button "post my order" again. The status of the order changes to "pending".
* To choose other businesses, client needs to start a new search. Order details and contact info of the client are saved on a server and cached locally on his phone or in his web browser.

### Business
1. Index page
Basic information about the application. Login/Registration menu. 
2. Registration  
* Fills out registration form (photo, first name, last name, e-mail, phone number, brief description).
* Clicks "Register". Registration form is passed to the server, where it is saved and filtered by city.

3. Orders
Once the service request is posted by the client, the order appears in order lists of the chosen businesses. 
All orders are split into two categories: "active" and "on hold". Active orders are the ones that haven't been taken yet by any of the businesses. Orders on hold have been already taken by another business, but not yet closed by a client.   

* Once chosen, a business gets notified about an active order. 
* Clicks on the active order and gets client's contact information. The order changes its status to "on hold". It's agreed that a business needs to contact a client within 15-20 minute, otherwise the order becomes active again.

## Web presence
Client can also use the app in a web browser (search for businesses and send requests). The website will also advertise the mobile app. Businesses use the mobile app to register and to get order notifications.
