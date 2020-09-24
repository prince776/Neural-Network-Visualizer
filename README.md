# Neural-Network-Visualizer

## Introduction
This project aims to visualize output of a basic 3 layered Neural Network(NN).

Neural networks are essentially universal function approximator, this program visualizes the output produced by a NN showing how, NN slowly estimates
the provided function, while providing some customizable hyperparameters.

![Pic](/demo.png)

## How to use
User can proveide a custom mathematical function `y = f(x, z)` (Note: automatic scaling is not provided for flexibility reason) or choose one from the
already provided functions, then press `Start` for the neural network to start estimating it.

The gray figure is the estimated function which rapidly changes as the NN fits the function with lower loss
meanwhile the colored figure is the desired output (which is visual representation of the function `f(x, z)` provided.

## Interactivity
1. 3D camera is provided to better examine the results. 

      * Left Click + Drag -> Rotate camera (about the clicked point)
      * Scroll -> to zoom in/out.
      
2. User can pause/resume the neural network to have a better look at its output.
3. Change learning rate to see how it affects the learning process.
4. Change iterations per epoch to speed up/slow down the learning process.
5. Change number of hidden nodes to see how it changes power of Neural Network to learn. 
