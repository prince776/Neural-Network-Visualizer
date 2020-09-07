public float ReLU(float x)
{
  if (x > 0)
    return x;
  return 0;
}

float dReLU(float x)
{
  if (x > 0)
    return 1;
   return 0;
}
public float linear(float x)
{
  return x;
}

float dLinear(float x)
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
    if (rows != m.rows || cols != m.cols)
    {
      println("Can't add matrices");
      return;
    }
    for(int i = 0; i < rows; i++)
      for(int j = 0; j < cols; j++)
        this.data[i][j] += m.data[i][j];
  }
  
  public void subtract(Matrix m){
    if (rows != m.rows || cols != m.cols)
    {
      println("Can't subtract matrices");
      return;
    }
    for(int i = 0; i < rows; i++)
      for(int j = 0; j < cols; j++)
        this.data[i][j] -= m.data[i][j];
  }
  
  public void scalarMultiply(Matrix m){
    if (rows != m.rows || cols != m.cols)
    {
      println("Can't scalar multiply matrices");
      return;
    }
    for(int i = 0; i < rows; i++)
      for(int j = 0; j < cols; j++)
        this.data[i][j] *= m.data[i][j];
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
  if (a.rows != b.rows || a.cols != b.cols)
  {
    println("Can't multiply matrices");
    return null;
  }
  Matrix result = new Matrix(a.rows, a.cols);
  for(int i = 0; i < result.rows; i++)
    for(int j = 0; j < result.cols; j++)
      result.data[i][j] = a.data[i][j] * b.data[i][j];

  return result;
}

public Matrix add(Matrix a, Matrix b){
  Matrix result = new Matrix(a.rows, a.cols);
  
  if (a.rows != b.rows || a.cols != b.cols)
  {
    println("Can't add matrices");
    return null;
  }
  
  for(int i = 0; i < result.rows; i++)
    for(int j = 0; j < result.cols; j++)
      result.data[i][j] = a.data[i][j] + b.data[i][j];

  return result;
}

public Matrix subtract(Matrix a, Matrix b){
  Matrix result = new Matrix(a.rows, a.cols);
  
  if (a.rows != b.rows || a.cols != b.cols)
  {
    println("Can't subtract matrices");
    return null;
  }
  
  for(int i = 0; i < result.rows; i++)
    for(int j = 0; j < result.cols; j++)
      result.data[i][j] = a.data[i][j] - b.data[i][j];

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
