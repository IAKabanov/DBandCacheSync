# DBandCacheSync
This is the test app, that emulates database and local cache.
## Requirements
* Element is one of the nodes of the tree.
* Element cannot be deleted. Only marked as deleted.
* If the element's parent is deleted, the inheritor will also be deleted.
* Deleted elements cannot be renamed.
* Database contains elements, that have their name.
* Database contains only one root of the tree.
* Database can be interacted with by applying cache.
* Database can be reset by button.
* Cache can contain elements, added from the database.
* Cache can be interacted with by adding an element into the cache.
* Cache can be interacted with by modifying an element in the cache.
* Cache can be interacted with by deleting (marking as deleted) an element in the cache.
* Cache can be reset.

## Database
The database is located in the right top corner. You can send an element from the database to the cache with a long-click. When there are no explicit connections between two nodes, they would be found at the same level in the cache.

![Database.](https://i.ibb.co/TWsKkxn/Screenshot-DB.png)

### Database reset
The database can be reset to the default state by pressing the reset button. Default values are shown on the screenshot. The cache will be reset as well.

![Database reset.](https://i.ibb.co/sjNtpDP/Screenshot-DBInteractions.png)

## Cache
The cache is located in the left top corner. It's empty by default.

![Cache](https://user-images.githubusercontent.com/111301619/234408866-e8c82892-8178-4787-9d35-f029cb99c6a2.png)

There are cache controls.

![Cache controls](https://i.ibb.co/Dfx40MG/Screenshot-Cache-Interactions.png)

### Add button
When you press the add button, add mode is enabled. Now you can long-click on any node in cache, you want to be the parent of the created node. Type the name of the new node. 
It won't be added if:
- the parent node is deleted
- adding node has the same name as in cache (Note. If the node has the same name in DB, but not in the cache, it will be added in the cache, but it won't be modified in the DB. If you try to make wrong connections, they won't be applied.)

### Delete button
When you press the delete button, delete mode is enabled. Now you can long-click on any node in the cache, you want to mark as deleted (unmark as deleted). The element will be marked as (D). All inheritors will be marked as deleted. Edit mode won't work for deleted elements. 

### Alter button
When you press alter button, edit mode is enabled. Now you can long-click on any node in the cache, you want to change the name. Type the new name of the node. Node won't be edited if its parent is deleted.

### Apply button
When you press apply button, merging elements has begun. Here are some merging rules:
- element with no changes won't be merged
- added elements that exist in DB won't be merged
- added elements with deleted parents won't be merged
- added elements that were deleted in the cache won't be merged
- edited elements with deleted parents won't be merged
- edited elements, that were deleted in DB
- deleted elements with deleted parents won't be merged
- undeleted elements, that were deleted in DB

### Reset button
When you press the reset button, the cache will be reset.
