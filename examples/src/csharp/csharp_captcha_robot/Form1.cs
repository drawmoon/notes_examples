using Emgu.CV;
using Emgu.CV.CvEnum;
using Emgu.CV.OCR;
using Emgu.CV.Structure;
using PaddleOCRSharp;
using System;
using System.Drawing;
using System.IO;
using System.Net;
using System.Windows.Forms;

namespace CaptchaRobot_win
{
    public partial class Form1 : Form
    {
        private PaddleOCREngine _paddle;
        private Tesseract _tesseract;

        public Form1()
        {
            InitializeComponent();
            InitOcr();
        }

        private void toolStripopenFile_Click(object sender, EventArgs e)
        {
            var openFileDialog = new OpenFileDialog
            {
                Filter = "*.*|*.bmp;*.jpg;*.jpeg;*.tiff;*.tif;*.png",
                InitialDirectory = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "images")
            };
            if (openFileDialog.ShowDialog() != DialogResult.OK)
            {
                return;
            }


            // 以灰度模式加载图像
            Mat image = CvInvoke.Imread(openFileDialog.FileName, ImreadModes.Grayscale);

            // 二值化，转换为黑白图像，以提高文字的识别概率
            CvInvoke.Threshold(image, image, 190, 255, ThresholdType.Binary);
            //CvInvoke.AdaptiveThreshold(image, image, 255, AdaptiveThresholdType.GaussianC, ThresholdType.Binary, 81, 17);

            // 模糊处理，模糊图像以减少噪声或帧中的小细节
            //CvInvoke.Blur(image, image, new Size(5, 5), new Point(-1, -1));
            //CvInvoke.GaussianBlur(image, image, new Size(5, 5), 5); // 高斯滤波
            CvInvoke.MedianBlur(image, image, 3); // 中值滤波

            // 去除干扰线
            // 参考：https://stackoverflow.com/questions/71425968/remove-horizontal-lines-with-open-cv

            //// 检测出图像的边缘
            //Mat imgEdge = new Mat();
            //CvInvoke.Canny(image, imgEdge, 50, 150);

            //// 检测直线
            //Mat imgLine = new Mat();
            //CvInvoke.HoughLines(imgEdge, imgLine, 1, Math.PI / 180, 100);

            //// 创建一个用于绘制结果的单通道图像
            //Mat result = new Mat(image.Size, DepthType.Cv8U, 1);
            //image.CopyTo(result);

            //// 遍历检测到的直线
            //for (int i = 0; i < imgLine.Rows; i++)
            //{
            //    Mat line = imgLine.Row(i);
            //    Array lineData = line.GetData();
            //    float rho = (float)lineData.GetValue(new int[] { 0, 0, 0 }); // 第一个元素是距离
            //    float theta = (float)lineData.GetValue(new int[] { 0, 0, 1 }); // 第二个元素是角度

            //    // 过滤出接近水平和垂直的线条
            //    if (Math.Abs(theta - Math.PI / 2) < 0.1 || Math.Abs(theta) < 0.1)
            //    {
            //        // 从极坐标转换为笛卡尔坐标
            //        Point pt1 = new Point();
            //        Point pt2 = new Point();

            //        double a = Math.Cos(theta);
            //        double b = Math.Sin(theta);
            //        double x0 = a * rho;
            //        double y0 = b * rho;
            //        pt1.X = (int)(x0 + 1000 * (-b));
            //        pt1.Y = (int)(y0 + 1000 * (a));
            //        pt2.X = (int)(x0 - 1000 * (-b));
            //        pt2.Y = (int)(y0 - 1000 * (a));

            //        // 绘制直线
            //        CvInvoke.Line(result, pt1, pt2, new MCvScalar(0, 0, 255), 3, LineType.AntiAlias);
            //    }
            //}

            //result.Save("image_line.png");


            //Mat horizontalKernel = CvInvoke.GetStructuringElement(ElementShape.Rectangle, new Size(25, 1), new Point(-1, -1));
            //CvInvoke.MorphologyEx(imgEdge, image, MorphOp.Close, horizontalKernel, new Point(-1, -1), 3, BorderType.Default, new MCvScalar());


            pictureBox1.Image = Image.FromFile(openFileDialog.FileName);
            pictureBox1.SizeMode = PictureBoxSizeMode.Zoom;

            pictureBox2.Image = Image.FromStream(new MemoryStream(image.ToImage<Bgr, byte>().ToJpegData()));
            pictureBox2.SizeMode = PictureBoxSizeMode.Zoom;


            #region TesseractORC

            {
                try
                {
                    _tesseract.SetImage(image);
                    if (_tesseract.Recognize() != 0)
                    {
                        throw new Exception("Failed to recognizer image");
                    }

                    richTextBox2.Text = _tesseract.GetUTF8Text();
                }
                catch (Exception exception)
                {
                    richTextBox2.Text = exception.Message;
                }
            }

            #endregion


            #region PaddleOCR


            {
                try
                {
                    //var imageByte = File.ReadAllBytes(openFileDialog.FileName);
                    //var bmp = new Bitmap(new MemoryStream(imageByte));

                    var img = image.ToImage<Bgr, byte>();
                    var bmp = new Bitmap(new MemoryStream(img.ToJpegData()));

                    OCRResult paddleResult = _paddle.DetectText(bmp);
                    richTextBox1.Text = paddleResult.Text;
                }
                catch (Exception exception)
                {
                    richTextBox1.Text = exception.Message;
                }
            }

            #endregion
        }

        private void InitOcr()
        {
            InitPaddleOrc();

            //ServicePointManager.Expect100Continue = true;
            //ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;

            InitEmgucvOcr(Tesseract.DefaultTesseractDirectory, "eng", OcrEngineMode.TesseractLstmCombined);
        }

        #region PaddleOCR 初始化

        private void InitPaddleOrc()
        {
            //var baseDir = EngineBase.GetRootDirectory();
            //var configPath = Path.Combine(baseDir, "inference");

            //var config = new OCRModelConfig
            //{
            //    det_infer = Path.Combine(configPath, "ch_PP-OCRv4_det_infer"),
            //    cls_infer = Path.Combine(configPath, "ch_ppocr_mobile_v2.0_cls_infer"),
            //    rec_infer = Path.Combine(configPath, "en_PP-OCRv4_rec_infer"),
            //    keys = Path.Combine(configPath, "ppocr_keys.txt")
            //};
            OCRModelConfig config = null;

            var parameter = new OCRParameter();
            _paddle = new PaddleOCREngine(config, parameter);
        }

        #endregion

        #region TesseractORC 初始化

        private static void TesseractDownloadLangFile(string folder, string lang)
        {
            var folderName = folder;
            if (!Directory.Exists(folderName))
            {
                Directory.CreateDirectory(folderName);
            }
            var dest = Path.Combine(folderName, string.Format("{0}.traineddata", lang));
            if (!File.Exists(dest))
                using (WebClient webclient = new WebClient())
                {
                    var source = Tesseract.GetLangFileUrl(lang);

                    Console.WriteLine(string.Format("Downloading file from '{0}' to '{1}'", source, dest));
                    webclient.DownloadFile(source, dest);
                    Console.WriteLine("Download completed");
                }
        }

        private void InitEmgucvOcr(string path, string lang, OcrEngineMode mode)
        {
            try
            {
                if (_tesseract != null)
                {
                    _tesseract.Dispose();
                    _tesseract = null;
                }

                if (string.IsNullOrEmpty(path))
                    path = Tesseract.DefaultTesseractDirectory;

                TesseractDownloadLangFile(path, lang);
                TesseractDownloadLangFile(path, "osd"); //script orientation detection

                _tesseract = new Tesseract(path, lang, mode);
            }
            catch (Exception e)
            {
                _tesseract = null;
                throw e;
            }
        }

        #endregion

    }
}
