# InstaEat
#### Instant food consumption and custom foods!


## **Custom Foods:**
#### Setting Custom Food:
1. Make sure you are doing this while logged into the game and not through the console.
2. Make sure you are Op or have the permission: `instaeat.command`.
3. Have a valid custom food material selected. Custom food materials include all types of food (except for Chorus Fruit) + Player Head.
4. Run the following command: `instaeat customfood set <hunger> <saturation> <chance>`
   - **hunger**: The amount of hunger points (hunger bar - on the bottom-right) the food will restore when consumed. Values: 0-20 (Integer).
   - **saturation**: The amount of saturation points the food will restore when consumed. Values: 0-20, precision of 0.1 (4.2 - ✓, 4.21 - ✕). Saturation is a hidden stat, for more information: [Hunger Mechanics](https://minecraft.gamepedia.com/Hunger#Mechanics)
   - **chance** : The chance the custom food will apply its effects. Explained in the [Custom Food Effects](https://github.com/DMan1629/InstaEat/blob/master/README.md#custom-food-effects) section. Values: 0-100 (Integer).

After setting a custom food, the information regarding its Hunger and Saturation will be displayed in the lore section of the item. The chance will **not** be displayed.

** ***Important***: Using this command on an item that is already a Custom Food will remove all its previous information (including Effects) and apply the new values afterwards.

#### Custom food information:
Run the command `instaeat customfood get` while holding a Custom Food to have the item's information displayed - hunger, saturation, chance.

#### Custom food removal:
Run the command `instaeat customfood remove` while holding a Custom Food to return it to a regular item.



## **Custom Food Effects:**
