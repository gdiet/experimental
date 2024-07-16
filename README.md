# Ants

This is a programming game where your code controls a colony of ants. The goal world domination. Your code competes against other players' code.

## How ants work - overview

Ants are surprisingly versatile animals.

To move, they jump straight up in the air and then open their paraglider to glide to a new location. The distance they can cover is limited by their weight, their power for jumping and by terrain features.

Ants can pick up food and bring it back to the anthill. An ant carrying food has additional weight.

From food in the anthill, new ants are bred.

Ants can fight other ants on the same field. For this, they can assume a defensive or an offensive stance. Killed ants become food.

Ants can raise or lower terrain features, according to their power.

Ants located on flooded cells drown, raising the terrain by 1.

## Game mechanics

### The board

The game is played on a rectangular grid of squares wrapped at both ends (a torus).

Each square has an elevation (integer). Cells with negative elevation are flooded with water. If ants end their turn in a flooded cell, they drown, elevating the cell by 1. If there are ants from different factions on the same flooded cell, the drowning is distributed equally, such that possibly the resulting elevation is larger than 0.

### The turns

The game is played in turns. Each turn, the game engine calls the code of each player, which can issue commands to its ants. Each turn consists of the multiple phases.

### The phases

1. Food: Food is generated on the board.
1. Empowerment: The power of each ant increases by 1.
1. Commands: The game engine calls the code of each player, which can issue commands to its ants.
1. Terrain: The ants raise or lower terrain features.
1. Movement: The ants move, possibly carrying food.
1. Combat: Ants from different factions on the same field fight each other. Killed ants become food.
1. Breeding: New ants are bred in the anthill.
1. Drowning: Ants on flooded cells drown, elevating the cell by 1.
1. Victory: The game engine checks for victory conditions.

### Phase: Food

Each turn, on a random location of each 10x10 board tile, one food item may appear. No food items appear if the random location

* is flooded,
* contains one or more food items or
* is an anthill.

### Phase: Empowerment

The power of each ant increases by 1.

### Phase: Commands

For each player, the game engine computes which parts of the board are visible. Then it hands over this visibility information together with some meta information to the player's code, which sends a response containing commands to its ants.

Player code must be stateless. In the response to the game engine's commands request, player code can store information in a state object, which is handed over to the player code in the next turn's commands request.

### Phase: Terrain

### Phase: Movement

### Phase: Combat

### Phase: Breeding

### Phase: Drowning

### Phase: Victory
