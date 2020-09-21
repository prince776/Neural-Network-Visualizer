public class NeuralNetwork {
  
  public int input_nodes,hidden_nodes,output_nodes;
  
  private Matrix weights_ih, weights_ho;
  private Matrix bias_h, bias_o;
  private float learningRate;
  /**
   * 
   * @param input_nodes
   * @param hidden_nodes
   * @param output_nodes
   */
  public NeuralNetwork(int input_nodes, int hidden_nodes, int output_nodes) {
    this.input_nodes = input_nodes;
    this.hidden_nodes = hidden_nodes;
    this.output_nodes = output_nodes;
    
    this.weights_ih = new Matrix(this.hidden_nodes, this.input_nodes);
    this.weights_ho = new Matrix(this.output_nodes, this.hidden_nodes);
    this.weights_ih.randomize();
    this.weights_ho.randomize();
    
    this.bias_h = new Matrix(hidden_nodes, 1);
    this.bias_o = new Matrix(output_nodes, 1);
    bias_h.randomize();
    bias_o.randomize();
    
    this.learningRate = 0.01f;
  }

  /**
   * 
   * @param input_array
   * @return
   */
  public float[] predict(float[] input_array){
    
    Matrix X = fromArray(input_array);

    // Hidden layer.
    Matrix Z1 = matrixMultiply(weights_ih, X);
    Z1.add(this.bias_h);    
    Matrix A1 = ReLU(Z1);
    
    // Output layer.
    Matrix Z2 = matrixMultiply(weights_ho, A1);
    Z2.add(bias_o);
    Matrix A2 = linear(Z2);
    
    return A2.toArray();

  }
  
  public Matrix predict(Matrix X)
  {
    Matrix Z1 = matrixMultiply(weights_ih, X);
    Z1.add(bias_h);
    Matrix A1 = ReLU(Z1);
    
    Matrix Z2 = matrixMultiply(weights_ho, A1);
    Z2.add(bias_o);
    Matrix A2 = linear(Z2);
    
    return A2;
  }
  /**
   * 
   * @param input_array
   * @param targets
   */
  public void train(float[] input_array , float[] targets){
    
    Matrix X = fromArray(input_array);
    Matrix Y = fromArray(targets);
    
    // Hidden layer.
    Matrix Z1 = matrixMultiply(weights_ih, X);
    Z1.add(this.bias_h);    
    Matrix A1 = ReLU(Z1);
    
    // Output layer.
    Matrix Z2 = matrixMultiply(weights_ho, A1);
    Z2.add(bias_o);
    Matrix A2 = linear(Z2);
    
    
    Matrix dZ2 = subtract(A2, Y);
    dZ2.scalarMultiply(dLinear(Z2));
    
    Matrix dW2 = matrixMultiply(dZ2, transpose(A1));
    Matrix db2 = dZ2.copy();
    
    Matrix dZ1 = matrixMultiply(transpose(weights_ho), dZ2);
    dZ1.scalarMultiply(dReLU(Z1));
    
    Matrix dW1 = matrixMultiply(dZ1, transpose(X));
    Matrix db1 = dZ1.copy();
    
    dW1.multiply(learningRate);
    db1.multiply(learningRate);
    dW2.multiply(learningRate);
    db2.multiply(learningRate);
    
    weights_ih.subtract(dW1);
    bias_h.subtract(db1);
    weights_ho.subtract(dW2);
    bias_o.subtract(db2);
  }
  
  public void train(Matrix X, Matrix Y)
  {
    // Hidden layer.
    Matrix Z1 = matrixMultiply(weights_ih, X);
    Z1.add(this.bias_h);    
    Matrix A1 = ReLU(Z1);
    
    // Output layer.
    Matrix Z2 = matrixMultiply(weights_ho, A1);
    Z2.add(bias_o);
    Matrix A2 = linear(Z2);
    
    Matrix dZ2 = subtract(A2, Y);
    dZ2.multiply(2);
    dZ2.scalarMultiply(dLinear(Z2));
    
    Matrix dW2 = matrixMultiply(dZ2, transpose(A1));
    dW2.multiply(1f / (float)Y.cols);
    Matrix db2 = new Matrix(bias_o.rows, bias_o.cols);
    for (int i = 0; i < db2.rows; i++)
    {
      float db = 0;
      for (int j = 0; j < dZ2.cols; j++)
        db += dZ2.data[i][j];
      db /= (float)dZ2.cols;
      db2.data[i][0] = db;
    }
    
    Matrix dZ1 = matrixMultiply(transpose(weights_ho), dZ2);
    dZ1.scalarMultiply(dReLU(Z1));
    
    Matrix dW1 = matrixMultiply(dZ1, transpose(X));
    dW1.multiply(1f / (float)Y.cols);
    Matrix db1 = new Matrix(bias_h.rows, bias_h.cols);
    for (int i = 0; i < db1.rows; i++)
    {
      float db = 0;
      for (int j = 0; j < dZ1.cols; j++)
        db += dZ1.data[i][j];
      db /= (float)dZ1.cols;
      db1.data[i][0] = db;
    }    
    
    dW1.multiply(learningRate);
    db1.multiply(learningRate);
    dW2.multiply(learningRate);
    db2.multiply(learningRate);
    
    weights_ih.subtract(dW1);
    bias_h.subtract(db1);
    weights_ho.subtract(dW2);
    bias_o.subtract(db2);
    
    dW1.print();
    println();
    
  }
  
  float[] getLossValues(float[] x, float[] y)
  {
    float[] lossVals = new float[2000 + 1];
    // weights from -10 to +10, with delta 0.1
    
    Matrix X = fromArray(x);
    Matrix Y = fromArray(y);
    
    Matrix W_ih = new Matrix(this.hidden_nodes, this.input_nodes);
    Matrix W_ho = new Matrix(this.output_nodes, this.hidden_nodes);
    W_ih.randomize();
    W_ho.randomize();
    
    Matrix B_h = new Matrix(hidden_nodes, 1, 0);
    Matrix B_o = new Matrix(output_nodes, 1, 0);
    B_h.randomize();
    B_o.randomize();
    
    int i = 0;
    for (int w = -1000; w <= 1000; w += 1)
    {
      W_ih.data[0][0] = w * 0.001;
      
      // FF
      Matrix Z1 = matrixMultiply(W_ih, X);
      Z1.add(B_h);
      Matrix A1 = ReLU(Z1);
      
      Matrix Z2 = matrixMultiply(W_ho, A1);
      Z2.add(B_o);
      Matrix A2 = linear(Z2);
      
      Matrix loss = subtract(A2, Y);
      loss.scalarMultiply(loss);
      
      lossVals[i] = loss.data[0][0];
      i++;
      
    }
    println(i);
   
    return lossVals; 
  }
  
  public float getLearningRate() {
    return learningRate;
  }

  public void setLearningRate(float learningRate) {
    this.learningRate = learningRate;
  }
  
}
