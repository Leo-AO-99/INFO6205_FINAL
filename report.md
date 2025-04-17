# Team 4 MCTS Report

## MCTS

We implement all the `TO BE IMPLEMENTED` part

We made the following key improvements to the MCTS algorithm:

1. Changed the data type of the `wins` variable from integer to double to support a more complex scoring system for wins and losses.
2. For backpropagation, we chose to use the parent node (parentNode) instead of the default `backPropagate` function.
3. Added a new function `double reward()` in State.java to allow different states to have different reward values. This enables us to customize a more precise scoring system for complex games like Othello, rather than just having binary win/loss outcomes.

These improvements allow our MCTS implementation to more accurately evaluate game states and make better decisions.


## TicTacToe

We implement all the `TO BE IMPLEMENTED` part

#### select

#### expand

#### simulate

Based on the UCT formula, select the next move and continue simulating until the game ends

#### backpropgate

the reward is determined based on the current state's player.



## Othello

Othello, also known as Reversi, is a strategic board game played on an 8x8 grid. The game involves two players who take turns placing discs on the board, with each player's discs being either black or white.

### Basic Rules:

1. **Setup**: The game begins with four discs placed in the center of the board in a square pattern, with two black discs and two white discs arranged diagonally.

2. **Gameplay**: 
   - Black moves first.
   - Players take turns placing one disc of their color on the board.
   - A valid move must "outflank" at least one of the opponent's discs.
   - Outflanking means placing a disc such that one or more of the opponent's discs are bordered at both ends by the player's discs in a straight line (horizontally, vertically, or diagonally).
   - All outflanked discs are flipped to the player's color.

3. **Valid Moves**: 
   - A move is only valid if it outflanks at least one opponent's disc.
   - If a player cannot make a valid move, their turn is skipped, and the opponent continues.
   - If neither player can make a valid move, the game ends.

4. **End of Game**: 
   - The game ends when the board is full or when neither player can make a valid move.
   - The player with the most discs of their color on the board wins.
   - If both players have the same number of discs, the game is a draw.

Othello combines simplicity of rules with depth of strategy, making it an excellent game for studying artificial intelligence and game theory.

### java file explanation

1. NextStep.java

The `NextStep.java` file defines a crucial data structure for the Othello game implementation. This class represents a potential move that a player can make during their turn.


2. PositionState.java

# PositionState.java Explanation

The `PositionState` enum serves to classify the current state of an Othello game, providing a clear way to determine if the game has ended and, if so, who the winner is. This is crucial for both the game logic and the MCTS algorithm implementation.




### Othello MCTS

In implementing the Monte Carlo Tree Search algorithm for Othello, we encountered several challenges and made improvements:

#### Initial Attempts and Challenges

Initially, we attempted to apply the same MCTS logic used for TicTacToe to the Othello game. However, we quickly discovered that this simple adaptation resulted in an AI with poor playing skills. Through deep analysis using DFS, we found that Othello has an enormous number of possible board states, far exceeding TicTacToe. This meant that purely random simulations had little practical value in such a large state space and couldn't effectively explore valuable game paths.

#### Game Characteristic Analysis

We observed that Othello games typically conclude within 27-31 moves, indicating that despite the large state space, the game depth is not particularly high. Based on this observation, we decided to run each simulation in the MCTS simulation phase until the game's conclusion, rather than simulating only a fixed number of moves. This approach allowed us to obtain more accurate endgame evaluations, leading to more effective decision-making.

#### Improvement Strategies

To enhance MCTS performance in Othello, we implemented the following improvements:

1. **Complete Game Simulation**: Each simulation runs until the game reaches its conclusion, providing a definitive win/loss result
2. **Heuristic Evaluation Function**: When selecting nodes for expansion, we incorporate position weights, prioritizing corners and edge positions
3. **Dynamic Exploration Parameter Adjustment**: We adjust the exploration constant in the UCT formula based on the progression of the game
4. **Position Value Matrix**: We implemented a position value matrix to evaluate board positions when a simulation reaches its conclusion. This matrix assigns different weights to different positions on the board:
   - Corners are assigned the highest values as they cannot be flipped once captured
   - Edge positions have higher values than center positions
   - Positions adjacent to corners are assigned negative values as they can give the opponent access to corners

   The value matrix is used during the backpropagation phase to provide a more nuanced evaluation than simple win/loss outcomes. This helps the algorithm make better decisions by considering the strategic value of different board positions, especially in the early and middle game phases where the final outcome is still uncertain.

These improvements enabled our MCTS algorithm to search more effectively within Othello's large state space, resulting in stronger game strategies.

