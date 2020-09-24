import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import org.quark.jasmine.*; 
import peasy.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class NeuralNetworkVisualizer extends PApplet {




 

final int width = 1280, height = 720;
final int gdWidth = 300, gdHeight = 300;

int scaleFactorY = 100;
int scaleFactorX = 100;

String equation = "0.0001 * x * x * x";
Expression expression;

public float f(float x, float z)
{  
  float y = expression.eval(x, z).answer().toFloat();
  return y;
}

// Neural 
NeuralNetwork nn;
int iterationsPerEpoch = 5;
//float[] lossVals;

// Rendering
PeasyCam cam;
PGraphics g1;

// GUI
ControlP5 cp5; 

Slider learningRateSlider;
Slider iterationsPerEpochSlider;
Button pauseResumeButton;
Button resetButton;

Textfield numInputNodes, numHiddenNodes, numOutputNodes;
Button startButton;

Textfield equationTextfield;
Button setEquationButton;

Button[] functionsButtons;
String[] functions;
int nFunctions = 5;

// Others
boolean pause = true;
boolean canChangePause = true;

boolean reset = false;
boolean canReset = true;

boolean setEquation = false;
boolean canSetEquation = true;

int nInput = 2, nHidden = 32, nOutput = 1;

public void init()
{
  nn = new NeuralNetwork(nInput, nHidden, nOutput);
  expression = Compile.expression(equation, true);
  //lossVals = nn.getLossValues(new float[]{1.0 / scaleFactorX , 1.0 / scaleFactorX}, new float[]{ f(1, 1) / scaleFactorY});
}

public void addGui()
{
  cp5 = new ControlP5(this); 
  cp5.setAutoDraw(false);
  
  learningRateSlider = cp5.addSlider("Set Learning Rate")
  .setRange(0.0001f, 0.1f)
  .setValue(0.02f)
  .setPosition(50, 50)
  .setSize(20,100)
  .setColorValue(0xffff88ff)
  .setColorLabel(0xffdddddd);
 
  iterationsPerEpochSlider = cp5.addSlider("Set Iterations per Epoch")
  .setRange(0,500)
  .setValue(iterationsPerEpoch)
  .setPosition(50, 250)
  .setSize(20,100)
  .setColorValue(0xffff88ff)
  .setColorLabel(0xffdddddd);
   
  pauseResumeButton = cp5.addButton("Pause")
  .setPosition(width / 2 - 80, 20)
  .setSize(60,30); 
  
  pauseResumeButton.onPress(new CallbackListener()
  {
    public void controlEvent(CallbackEvent event)
    {
      if (canChangePause)
      {
        canChangePause = false;
        pause = !pause;
      }
    }
  });
  
  pauseResumeButton.onRelease(new CallbackListener()
  {
    public void controlEvent(CallbackEvent event)
    {
      canChangePause = true;
    }
  });

  resetButton = cp5.addButton("Reset")
  .setPosition(width / 2 + 20, 20)
  .setSize(60,30); 
  
  resetButton.onPress(new CallbackListener()
  {
    public void controlEvent(CallbackEvent event)
    {
      if (canReset)
      {
        canReset = false;
        reset = true;
      }
    }
  });
  
  resetButton.onRelease(new CallbackListener()
  {
    public void controlEvent(CallbackEvent event)
    {
      canReset = true;
      reset = false;
    }
  });
  
  PFont font = createFont("arial", 12);
  
  numInputNodes = cp5.addTextfield("Input")
  .setPosition(210, 50)
  .setSize(50, 20)
  .setText(nn.input_nodes + "")
  .setFont(font)
  .setColor(color(255, 0, 0));

  numHiddenNodes = cp5.addTextfield("Hidden")
  .setPosition(270, 50)
  .setSize(50, 20)
  .setText(nn.hidden_nodes + "")
  .setFont(font)
  .setColor(color(255, 0, 0));

  numOutputNodes = cp5.addTextfield("Output")
  .setPosition(330, 50)
  .setSize(50, 20)
  .setText(nn.output_nodes + "")
  .setFont(font)
  .setColor(color(255, 0, 0));
  
  cp5.addTextlabel("Neural Network Layers")
  .setPosition(210, 20)
  .setSize(50, 20)
  .setText("Neural Network Layers")
  .setFont(createFont("arial", 16))
  .setColor(color(255, 0, 0));

  startButton = cp5.addButton("Start")
  .setPosition(265, 100)
  .setSize(60,30); 
  
  startButton.onPress(new CallbackListener()
  {
    public void controlEvent(CallbackEvent event)
    {
      if (canReset)
      {
        canReset = false;
        reset = true;
        canChangePause = true;
        pause = false;
      }
    }
  });
  
  startButton.onRelease(new CallbackListener()
  {
    public void controlEvent(CallbackEvent event)
    {
      canReset = true;
    }
  });
  
  cp5.addTextlabel("Enter mathematical function for NN to estimate")
  .setPosition(width - 400, 20)
  .setSize(50, 20)
  .setText("Enter mathematical function for NN to estimate")
  .setFont(createFont("arial", 16))
  .setColor(color(255, 0, 0));
  
  cp5.addTextlabel("Y = ")
  .setPosition(width - 400, 55)
  .setSize(50, 20)
  .setText("Y = ")
  .setFont(createFont("arial", 16))
  .setColor(color(255, 0, 0));
  
  equationTextfield = cp5.addTextfield("")
  .setPosition(width - 360, 50)
  .setSize(300, 30)
  .setText(equation)
  .setFont(createFont("arial", 16))
  .setColor(color(255, 0, 0));
  
  setEquationButton = cp5.addButton("Set")
  .setPosition(width - 250, 100)
  .setSize(60,30); 
 
  setEquationButton.onPress(new CallbackListener()
  {
    public void controlEvent(CallbackEvent event)
    {
      if (canSetEquation)
      {
        canSetEquation = false;
        setEquation = true;
      }
    }
  });
  
  setEquationButton.onRelease(new CallbackListener()
  {
    public void controlEvent(CallbackEvent event)
    {
      canSetEquation = true;
      setEquation = false;
    }
  }); 
  
  functions = new String[nFunctions];
  functionsButtons = new Button[nFunctions];
  
  functions[0] = "100 * sin(0.05 * x)";
  functions[1] = "0.01 * x * x + 0.01 * z * z";
  functions[2] = "0.01 * x * x + 0.01 * x * z";
  functions[3] = "0.01 * x * z";
  functions[4] = "0.0001 * x * x * x";
  
  for (int i = 0; i < nFunctions; i++)
  {
    functionsButtons[i] = cp5.addButton(functions[i])
    .setPosition(width - 300, 200 + i * 50)
    .setSize(200,40)
      .setFont(createFont("arial", 16));
    final String s = functions[i];
    functionsButtons[i].onPress(new CallbackListener()
    {
      public void controlEvent(CallbackEvent event)
      {
        if (equation.equals(s))
          return;
        equation = s;
        expression = Compile.expression(equation, true);
        equationTextfield.setText(equation);
      }
    });  
  }
  
}

public void setup()
{
  
  frameRate(60);
  cam = new PeasyCam(this, width / 2, height / 2 - 50, -100, 600);
  g1 = createGraphics(gdWidth, gdHeight, P3D);   
  
  init();
  addGui();
}

public void tick()
{
  // Parse and set Hyperparameters
  String lrValue = String.format("%.4f", learningRateSlider.getValue());
  learningRateSlider.setValueLabel(lrValue);
  nn.setLearningRate(Float.parseFloat(lrValue));
  
  iterationsPerEpoch = (int)iterationsPerEpochSlider.getValue();
  iterationsPerEpochSlider.setValueLabel(iterationsPerEpoch + "");
  
  if (pause)
    pauseResumeButton.setLabel("Resume");
  else
    pauseResumeButton.setLabel("Pause");
  if (!pause)
    trainNN();
    
  if (reset)
  {
    reset = false;
    init();
  }
  
  nInput = nn.input_nodes;
  nHidden = nn.hidden_nodes;
  nOutput = nn.output_nodes;
  
  try
  {
    nInput = Integer.parseInt(numInputNodes.getText());
    nHidden = Integer.parseInt(numHiddenNodes.getText());
    nOutput = Integer.parseInt(numOutputNodes.getText());
  }catch(NumberFormatException e)
  {
    println("Bad Number");
  }
  //if (nInput == 0)
    nInput = nn.input_nodes;
  if (nHidden == 0)
    nHidden = nn.hidden_nodes;
  //if (nOutput == 0)
    nOutput = nn.output_nodes;
  
  if (!numInputNodes.isFocus())  numInputNodes.setText(nInput + "");
  if (!numHiddenNodes.isFocus())  numHiddenNodes.setText(nHidden + "");
  if (!numOutputNodes.isFocus())  numOutputNodes.setText(nOutput + "");
  
  if (setEquation)
  {
    String newEquation = equationTextfield.getText();
    String oldEquation = "" + equation;
    try
    {
      equation = newEquation;
      expression = Compile.expression(equation, true);
      // for test
    }catch(Exception e)
    {
      equation = oldEquation;
      expression = Compile.expression(equation, true);
    }
    equationTextfield.setText(equation);
    setEquation = false;
  }
  
}

public void draw()
{
  tick();
  
  background(0);
  
  translate(width / 2, height / 2);
  noFill();
    
  // Create Axes
  strokeWeight(1);
  stroke(255, 0, 0);  
  line(-1000, 0, 0, 1000, 0, 0);
  stroke(0, 255, 0);
  line(0, -1000, 0, 0, 1000, 0);
  stroke(0, 0, 255);
  line(0, 0, -1000, 0, 0, 1000);

  // Create Actual Graph
  strokeWeight(10);
  beginShape();
  for (int i = -100; i <= 100; i++)
  {
    for (int j = -100; j <= 100; j++)
    {
      stroke(i + 100, j + 100, -i + j); 
      vertex(i, -f(i, j), j);
    }
  }
  endShape();
  
  //Plot by nn
  stroke(100, 150);
  strokeWeight(5);
  
  Matrix X = new Matrix(2, 40401);
  int k = 0;
  for (int i = -100; i <= 100; i+=1)
  {
    for (int j = -100; j <= 100; j+= 1)
    {
      X.data[0][k] = (float)i / scaleFactorX;
      X.data[1][k] = (float)j / scaleFactorX;
      k++;
      //vertex(i, -nn.predict(new float[]{(float) i / scaleFactorX, (float) j / scaleFactorX})[0] * scaleFactorY, j);
    }
  }
  Matrix output = nn.predict(X);
  
  beginShape();  
  k = 0;
  for (int i = -100; i <= 100; i+=1)
  {
    for (int j = -100; j <= 100; j+= 1)
    {
      vertex(X.data[0][k] * scaleFactorX, -output.data[0][k] * scaleFactorY, X.data[1][k]  * scaleFactorX);
      k++;
    }
  }
  endShape();
  
  cam.beginHUD();
  cp5.draw();
  
  
  /**
  g1.beginDraw();
  
  g1.background(100);
  g1.translate(gdWidth / 2, gdHeight / 2);
  
  g1.strokeWeight(1);
  g1.stroke(255, 0, 0);  
  g1.line(-gdWidth / 2, 0, 0, gdWidth / 2, 0, 0);
  g1.stroke(0, 255, 0);
  g1.line(0, -gdHeight / 2, 0, 0, gdHeight / 2, 0);
  g1.stroke(0, 0, 255);
  
  g1.noFill();
  // Draw graph
  g1.beginShape();
  
  int i = 0;
  for (int w = -100; w <= 100; w++)
  {
    println(lossVals[i]);
    g1.vertex(w, -lossVals[i++], 0);
  }
  
  g1.endShape();
  
  g1.endDraw();
  
  //
  //image(g1, width - gdWidth - 10, 250);
  **/
  
  cam.endHUD();

}

public void trainNN()
{

  for (int i = 0; i < iterationsPerEpoch; i++)
  {
    float x = (float)(Math.random() * 2 - 1) * scaleFactorX;
    float z = (float)(Math.random() * 2 - 1) * scaleFactorX;
    float y = f(x, z);
    x /= scaleFactorX;
    z /= scaleFactorX;
    y /= scaleFactorY;
    
    nn.train(new float[]{x, z}, new float[]{y});
  }
}
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
public float ReLU(float x)
{
  if (x > 0)
    return x;
  return 0;
}

public float dReLU(float x)
{
  if (x > 0)
    return 1;
   return 0;
}
public float linear(float x)
{
  return x;
}

public float dLinear(float x)
{
  return 1;
}

public float sigmoid (float x){
  return (float)(1f/(1+Math.exp(-x)));
}

public float dsigmoid (float x){
  return x * (1 - x);
}

public class Matrix {
  
  private int rows, cols;
  private float data[][]; 
  
  public Matrix(int rows, int cols){
    this.rows = rows;
    this.cols = cols;
    this.data = new float[rows][cols];
    
    for (int i = 0; i < rows; i++)
      for (int j = 0; j < cols; j++)
        data[i][j] = 0;
  }
  
  public Matrix(int rows, int cols, int val){
    this.rows = rows;
    this.cols = cols;
    this.data = new float[rows][cols];
    
    for (int i = 0; i < rows; i++)
      for (int j = 0; j < cols; j++)
        data[i][j] = val;
  }
  
  public Matrix(float[][] data)
  {
    this.rows = data.length;
    this.cols = data[0].length;
    this.data = data;
  }
  
  public void print()
  {
    for (int i = 0; i < rows; i++)
    {
      for (int j = 0; j < cols; j++)
        System.out.print(data[i][j] + " ");
      println();
    }
  }
  
  public Matrix copy(){
    Matrix m  = new Matrix(rows, cols);
    for(int i = 0; i < rows; i++){
      for(int j = 0; j < cols; j++){
        m.data[i][j] = this.data[i][j];
      }
    }
    return m;
  }
  
  public void randomize(){
    for(int i = 0; i < rows; i++)
    {
      for(int j = 0; j < cols; j++)
      {
        this.data[i][j] =(float)(Math.random());
        
        if(data[i][j] == 0)
        {
          if(0.5f < Math.random())
            data[i][j] += 0.01f;
          else
            data[i][j] -= 0.01f;  
        }
      }
    }
  }
  
  public float[] toArray(){
    float[] arr = new float[rows * cols];
    for(int i = 0; i < rows; i++)
      for(int j = 0; j < cols; j++)
        arr[i*cols+j] = (float)this.data[i][j];
    
    return arr;
  }
  
  // Scalar operations
  public void multiply(float n){
    for(int i = 0; i < rows; i++)
      for(int j = 0; j < cols; j++)
        this.data[i][j] *= n;
  }
  
  public void add(float n){
    for(int i = 0; i < rows; i++)
      for(int j = 0; j < cols; j++)
        this.data[i][j] += n;
  }
  
  // Matrix operations
  public void add(Matrix m){
    //if (rows != m.rows || cols != m.cols)
    //{
    //  println("Can't add matrices");
    //  return;
    //}
    
    for(int i = 0; i < rows; i++)
      for(int j = 0; j < cols; j++)
        this.data[i][j] += m.data[i % m.rows][j % m.cols];
  }
  
  public void subtract(Matrix m){
    //if (rows != m.rows || cols != m.cols)
    //{
    //  println("Can't subtract matrices");
    //  return;
    //}
    
    for(int i = 0; i < rows; i++)
      for(int j = 0; j < cols; j++)
        this.data[i][j] -= m.data[i % m.rows][j % m.cols];
  }
  
  public void scalarMultiply(Matrix m){
    //if (rows != m.rows || cols != m.cols)
    //{
    //  println("Can't scalar multiply matrices");
    //  return;
    //}
    
    for(int i = 0; i < rows; i++)
      for(int j = 0; j < cols; j++)
        this.data[i][j] *= m.data[i % m.rows][j % m.cols];
  }
  
  public void matrixMultiply(Matrix m){
    
    if(this.cols != m.rows)
    {
      println("Can't cross multiply given matrix!");
      return ;
    }
    
    Matrix result = new Matrix(this.rows, m.cols);
    
    for(int i = 0; i < result.rows; i++)
    {
      for(int j = 0; j < result.cols; j++)
      {
        float sum = 0;
        for(int k = 0; k < this.cols; k++){
          sum += this.data[i][k] * m.data[k][j];
        }
        result.data[i][j] = sum;
      }
    }
    this.data = result.data;
    this.rows = result.rows;
    this.cols = result.cols;
  }
    
  public int getRows() {
    return rows;
  }

  public int getCols() {
    return cols;
  }

  public float[][] getData() {
    return data;
  }
  
}

// STATIC METHODS ////////////////////////////////////

public Matrix transpose(Matrix m){
  Matrix result = new Matrix(m.cols, m.rows);
  for(int i = 0; i < m.rows; i++)
    for(int j = 0; j < m.cols; j++)
      result.data[j][i] = m.data[i][j];
      
  return result;
}

public Matrix scalarMultiply(Matrix a, Matrix b){
  //if (a.rows != b.rows || a.cols != b.cols)
  //{
  //  println("Can't multiply matrices");
  //  return null;
  //}
  int maxRows = Math.max(a.rows, b.rows);
  int maxCols = Math.max(a.cols, b.cols);
    
  Matrix result = new Matrix(maxRows, maxCols);
  for(int i = 0; i < result.rows; i++)
    for(int j = 0; j < result.cols; j++)
      result.data[i % maxRows][j % maxCols] = a.data[i % a.rows][j % a.cols] * b.data[i % b.rows][j % b.cols];

  return result;
}

public Matrix add(Matrix a, Matrix b){  
  //if (a.rows != b.rows || a.cols != b.cols)
  //{
  //  println("Can't add matrices");
  //  return null;
  //}
  int maxRows = Math.max(a.rows, b.rows);
  int maxCols = Math.max(a.cols, b.cols);
  
  Matrix result = new Matrix(maxRows, maxCols);
  for(int i = 0; i < result.rows; i++)
    for(int j = 0; j < result.cols; j++)
      result.data[i % maxRows][j % maxCols] = a.data[i % a.rows][j % a.cols] + b.data[i % b.rows][j % b.cols];

  return result;
}

public Matrix subtract(Matrix a, Matrix b){
  //if (a.rows != b.rows || a.cols != b.cols)
  //{
  //  println("Can't subtract matrices");
  //  return null;
  //}
  int maxRows = Math.max(a.rows, b.rows);
  int maxCols = Math.max(a.cols, b.cols);
  
  Matrix result = new Matrix(maxRows, maxCols);
  for(int i = 0; i < result.rows; i++)
    for(int j = 0; j < result.cols; j++)
      result.data[i % maxRows][j % maxCols] = a.data[i % a.rows][j % a.cols] - b.data[i % b.rows][j % b.cols];

  return result;
}

public Matrix matrixMultiply(Matrix a, Matrix b){
  
  if(a.cols != b.rows)
  {
    println("Can't multiply matrices");
    return null;
  }
  
  Matrix result = new Matrix(a.rows, b.cols);
  for(int i = 0; i < result.rows; i++)
  {
    for(int j = 0; j < result.cols; j++)
    {
      float sum = 0;
      for(int k = 0; k < a.cols; k++)
        sum += a.data[i][k] * b.data[k][j];
      result.data[i][j]=sum;
    }
  }
  return result;
}

public Matrix fromArray(float[] arr){
  Matrix result = new Matrix(arr.length, 1);
  for(int i = 0; i < arr.length; i++)
    result.data[i][0] = arr[i];

  return result;
}

public Matrix ReLU(Matrix m)
{
  Matrix result = new Matrix(m.rows, m.cols);
  for (int i = 0; i < m.rows; i++)
    for (int j = 0; j < m.cols; j++)
      result.data[i][j] = ReLU(m.data[i][j]);
  
  return result;
}

public Matrix dReLU(Matrix m)
{
  Matrix result = new Matrix(m.rows, m.cols);
  for (int i = 0; i < m.rows; i++)
    for (int j = 0; j < m.cols; j++)
      result.data[i][j] = dReLU(m.data[i][j]);
  
  return result;
}


public Matrix linear(Matrix m)
{
  Matrix result = new Matrix(m.rows, m.cols);
  for (int i = 0; i < m.rows; i++)
    for (int j = 0; j < m.cols; j++)
      result.data[i][j] = linear(m.data[i][j]);
  
  return result;
}

public Matrix dLinear(Matrix m)
{
  Matrix result = new Matrix(m.rows, m.cols);
  for (int i = 0; i < m.rows; i++)
    for (int j = 0; j < m.cols; j++)
      result.data[i][j] = dLinear(m.data[i][j]);
  
  return result;
}

public Matrix sigmoid(Matrix m)
{
  Matrix result = new Matrix(m.rows, m.cols);
  for (int i = 0; i < m.rows; i++)
    for (int j = 0; j < m.cols; j++)
      result.data[i][j] = sigmoid(m.data[i][j]);
  
  return result;
}

public Matrix dSigmoid(Matrix m)
{
  Matrix result = new Matrix(m.rows, m.cols);
  for (int i = 0; i < m.rows; i++)
    for (int j = 0; j < m.cols; j++)
      result.data[i][j] = dsigmoid(m.data[i][j]);
  
  return result;
}
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
  
  public float[] getLossValues(float[] x, float[] y)
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
      W_ih.data[0][0] = w * 0.001f;
      
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
  public void settings() {  size(1280, 720, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "NeuralNetworkVisualizer" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
