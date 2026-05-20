# Lost Time: Rewrite

**Lost Time: Rewrite** is a Java tactical RPG / adventure prototype inspired by classic Fire Emblem-style grid combat, D&D-style dice mechanics, and story-driven fantasy RPGs.

The game follows **Art Forger**, **Penelope Godwinson**, and **Dean Lokka** as they grow from village kids chasing adventure into a party caught in a much larger conflict involving ancient ruins, a mysterious sword called **The Creation**, the rise of corruption, and the forgotten threat of **Marrtyme**.

------------------------------------------------------------------------------------------------------------------------------------------------

## Story Summary

Long ago, the demonic god of chaos, **Marrtyme**, nearly corrupted the world of **Epos**. A band of ancient heroes sealed him away using a divine weapon known as **The Creation**, a sword tied to the god of creation, **Quinn**.

Thousands of years later, in the humble village of **Cerebella**, three childhood friends discover an old sword in forgotten ruins. When Art touches it, a white flash tears through the ruins, unknowingly beginning a chain of events that will pull them into a much larger conflict.

Act One follows the party as they explore thier adventure!
The current build ends with **End of Act One**.

---------------------------------------------------------------------------------------------------------------------------------------------------

## Main Characters

### Art Forger
The protagonist. A swordsman with a strong moral compass. Art is not fully confident in himself yet, but he does what he believes is right. He becomes bonded to the mysterious sword known as **The Creation**.

### Penelope Godwinson
A kind, careful, protective cleric. Penelope is shy at times, but emotionally strong. She watches over the party and often notices danger before others do.

### Dean Lokka
An energetic archer with dreams of becoming a legendary hero. Dean is comedic, dramatic, and eager for adventure. His heroic aspirations are central to his long-term story arc.

### Tali Sin
Former leader of the Golden Sinners, known publicly as “The King.” Tali originally built the group for survival, but Cael twisted it into something crueler. After Cael is defeated, she joins the party.

### William Winters
A calm, intelligent mage and scholar. William knows more about The Creation, relics, and ancient magic than he initially admits. He joins after saving the party during the Golem Seal Trap.

---------------------------------------------------------------------------------------------------------------------------------------------------

## Gameplay Systems

### Exploration
The game uses tile-based exploration across multiple map states:

- **Overworld**
- **Town**
- **Exploration maps**
- **Camp**
- **Battle maps**

The player moves tile-by-tile and interacts with special tiles such as towns, quest boards, events, shops, exits, and story objects.

---

### Tactical Combat

Combat uses a grid-based battle system like in fire embelem.

Current battle features:

- Player phase / enemy phase structure
- Unit selection cursor
- Movement range preview
- Action menu after movement
- Attack / Skill / Wait
- Target selection
- Attack forecast
- Zoom-in combat scene
- Floating hit/miss/damage text
- Enemy AI roles
- Battle objectives
- Defeat screen with retry option

---

### D&D-Inspired Combat

Attacks use dice-style logic:

- d20-style hit checks
- Armor Class comparison
- Weapon-based damage dice
- Crit chance based on Luck
- Lucky Break survival chance
- Physical and magical damage types

---------------------------------------------------------------------------------------------------------------------------------------------------

### Skills and Mana

Each character can have a skill.

Skills cost mana and can be used as long as the unit has enough MP.

---------------------------------------------------------------------------------------------------------------------------------------------------

### Equipment

Party members can own and equip multiple weapons.

Current equipment features:

- Party-wide equipment menu
- Class-filtered shop weapons
- Equipped weapon affects combat
- Weapon inventories save/load properly

---------------------------------------------------------------------------------------------------------------------------------------------------

### Quest System

The game includes a quest board and story-driven quest flow.

The quest board supports:

- Accept / decline prompts
- One active quest at a time
- Quest completion flags
- Rewards
- Quest log display
- Story progression hooks

---------------------------------------------------------------------------------------------------------------------------------------------------

### Camp System

Camp allows the party to:

- Rest and recover mana
- Trigger group conversations
- Trigger one-on-one bond conversations
- Reflect on story events

Camp conversations change based on chapter and can be used for future personal quests.

---------------------------------------------------------------------------------------------------------------------------------------------------

The game currently saves and loads many values:

---------------------------------------------------------------------------------------------------------------------------------------------------

## Controls

| Key | Action 

| Arrow Keys | Move / Navigate menus 

| Enter | Confirm / Interact / Advance dialogue 

| Esc | Back / Cancel

| E | Equipment menu 

| Q |  Status screen 

| F |  Camp menu 

| S | Save game 

| L | Load game 

Some keys may still be adjusted during development



--------------------------------------------------------------------------------------------------------------------------------------------------------------

## Current Development Status: Ongoing!
### Still Planned for the future
- Sprite and pixel art support hopefully!
- Real portraits and expressions
- Larger maps / camera system
- Full inventory and consumables
- More advanced enemy AI
- More classes and personal quests
- Act Two and Three story content
- Title screen polish
- Sound and music
- Combat Animation polish
- The Final Rewrite 

--------------------------------------------------------------------------------------------------------------------------------------=

## Notes

This project is still in active development. Many systems are functional but subject to refactoring, polishing, and expansion.
