# InstaEat
#### Instant food consumption and custom foods!

InstaEat immediately consumes the food item when right clicking. All the vailla Minecraft special effects - status effects, advancement progression, planting - have not changed.

Food cannot be eaten when the both the hunger and saturation bars are full(#).

The foods are consumed upon right clicking, so holding the right button will not not consume multiples. To consume few times in a row, one must reclick.

InstaEat also allows the creation of "new" food items using the Custom Foods system.
***
## **Custom Foods:**
Custom Foods are food items with new consumption values. This allows creation of "new" foods, even foods with [Custom Effects](#custom-food-effects).

#### Setting Custom Food:
1. Make sure you are doing this while logged into the game and not through the console.
2. Make sure you are Op or have the permission: `instaeat.command`.
3. Have a valid custom food material selected. Custom food materials include all types of food (except for Chorus Fruit) + Player Head.
4. Run the following command: `instaeat customfood set <hunger> <saturation> <chance>`
   - **hunger**: The amount of hunger points (hunger bar - on the bottom-right) the food will restore when consumed. Values: 0-20 (Integer).
   - **saturation**: The amount of saturation points the food will restore when consumed. Values: 0-20, precision of 0.1 (Example: 4 - ✓, 4.2 - ✓, 4.21 - ✕). Saturation is a hidden stat, for more information: [Hunger Mechanics](https://minecraft.gamepedia.com/Hunger#Mechanics)
   - **chance** : The chance the custom food will apply its effects. Explained in the [Custom Food Effects](#custom-food-effects) section. Values: 0-100 (Integer). ***Notice***: A Custom Food with a chance of 100 **will** be consumed even if both hunger and saturation bars are full(#).

After setting a custom food, the information regarding its Hunger and Saturation will be displayed in the lore section of the item. The chance will **not** be displayed.

###### *Example*: While holding a Baked Potato - `instaeat customfood set 5 2 50`

** ***Important***: Using this command on an item that is already a Custom Food will remove all its previous information (including Effects) and apply the new values afterwards.

#### Custom Food information:
Run the command `instaeat customfood get` while holding a Custom Food to have the item's information displayed - hunger, saturation, chance.

#### Custom Food removal:
Run the command `instaeat customfood remove` while holding a Custom Food to return it to a regular item.
***
## **Custom Food Effects:**
Custom Food Effects are built-in Minecraft [status effects](https://minecraft.gamepedia.com/Status_effect#Summary_of_effects) that can be applied when a Custom Food is consumed. When a Custom Food with Effects is consumed, all of its Effects are applied to the consumer based on the Food's chance stat - 60 means there's a 60% chance the Effects will be applied; 100 means the Effects will **definitely** be applied.

#### Adding Custom Food Effects:
1. Make sure you are doing this while logged into the game and not through the console.
2. Make sure you are Op or have the permission: `instaeat.command`.
3. Make sure you are holding a Custom Food. The Effects can only be applied to Custom Foods.
4. Start typing: `instaeat customfoodeffect add `, and be greeted with 2 options: `clear` and `give`
   - **clear**: The effect set by this option will be **remvoed** from the player. A good example for this is the [Honey Bottle](https://minecraft.gamepedia.com/Honey_Bottle#Usage), which removes the Poison effect upon consumption.
   - **give**: The effect set by this option will be **added** to the player. Works same as Potions.

Continue to get the list of all possible [status effects](https://minecraft.gamepedia.com/Status_effect#Summary_of_effects). If you've chosen the `clear` option - this is it. If you chose the `give` option, you must now specify the *duration* and afterwards the *amplifier*:
   - **duration**: How long, in seconds, the effect will last. Values: 1-1 Million (Integer).
   - **amplifier**: The level of the effect's strength - same as potion levels. Values: 1-256 (Integer).

** ***Important***: If the Food already has a version of the effect type, it will not allow readding. Also, due to Minecraft's mechanics, effects with greater duration/amplifier will prevent applying new effects of the same type or will overwrite an existing one.

###### *Example*: While holding the [Baked Potato](#example-while-holding-a-baked-potato---instaeat-customfood-set-5-2-50) from before - `instaeat customfoodeffect add give ABSORPTION 10 2`, and afterwards `instaeat customfoodeffect add clear WEAKNESS`. After consuming the [Baked Potato](#example-while-holding-a-baked-potato---instaeat-customfood-set-5-2-50), there's a 50% chance that the player will get ABSORPTION 2 for 10 seconds and his WEAKNESS status effect will be removed.

#### Custom Food Effects information:
Run the command `instaeat customfoodeffect get` while holding a Custom Food with Effects to have all of the Food's Effects listed along with their information.

#### Custom Food Effects removal:
- Run the command `instaeat customfoodeffect remove ` followed by an effect name while holding a Custom Food with Effects to remove the given effect.
- Run the command `instaeat customfoodeffect remove_all` while holding a Custom Food with Effects to remove **all** if its Effects.

***

If any problems arise from the plugin, please open an [issue](https://github.com/DMan1629/InstaEat/issues) or write in the plugin's [Spigot discussion page]().
