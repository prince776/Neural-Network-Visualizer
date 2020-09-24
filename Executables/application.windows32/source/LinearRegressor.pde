public class LinearRegressor
{
  public Matrix weights;
  public float bias;
  
  public float learningRate;
  
  public int trainCount = 0;
  
  public LinearRegressor(int inputFeatures)
  {
    weights = new Matrix(inputFeatures, 1);
    weights.randomize();
    bias = (float)(Math.random() * 1f);
    
    this.learningRate = 0.01f;
  }
  
  // single sample input
  public float predict(float[] input)
  {    
    Matrix X = fromArray(input);
    Matrix wT = transpose(weights);
    
    Matrix Z = matrixMultiply(wT, X);
    Z.add(bias);
    
    Matrix A = linear(Z);
    return A.data[0][0];
    
  }
  
  // single sample input
  public void train(float[] input, float output)
  {
    float A = predict(input);
    
    float dZ = 2 * (A - output) * dLinear(A);
    dZ *= learningRate;
    
    Matrix dW = fromArray(input);
    dW.multiply(dZ);
    
    weights.subtract(dW);
    bias -= dZ;
    
    trainCount++;
    //println(trainCount);
  }
  
  // Some extra methods
  // Calculates loss value for given values of w and b. w and input can be only 1D for now
  public float[][] getLossValues(float[] input, float output, float[] ws, float[] bs)
  {
    int n = ws.length;
    int m = bs.length;
    
    float[][] lossValues = new float[n][m];
    
    for (int i = 0; i < n; i++)
    {
      for (int j = 0; j < m; j++)
      {
        float A = input[0] * ws[i] + bs[j];
        lossValues[i][j] = (A - output) * (A - output);
      }
    }
    
    return lossValues;
  }
  
}
