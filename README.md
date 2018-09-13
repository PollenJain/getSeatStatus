# getSeatStatus

### driver_interface_route_list_1.0.0 :
First working version of route list data loading from firestore and populated on recycler view.<br>
This version retrieves data from cloud firestore database (https://console.firebase.google.com/project/firestore-recycler-view/database)
and displays it on the recycler view. When a new route is added in the *routes* document the list gets updated (not asynchronously though). To see the updated list of routes open the app again.


### driver_interface_route_list_1.0.1 :
i) Publishes the selected route and start time information to all the commuters for that route.
ii) Able to retrieve stops for selected route from firestore.
iii) Displays in recyclerview/cardview.




