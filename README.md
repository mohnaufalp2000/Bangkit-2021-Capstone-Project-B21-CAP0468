## Bangkit 2021 Capstone Project (Fresco : Fruit and Plant Recognition) by B21-CAP0468

We created a project named "Fresco" to fulfill a graduation requirements of Bangkit Academy 2021. Our team consist of android team, cloud engineer, and machine learning team.
This project has the from the application where it can recognize the name of fruit and plant, and also distinguish between fresh fruit and rotten fruit.

<table><tr>
<td> <img src="https://user-images.githubusercontent.com/49554106/121322897-9086bc00-c939-11eb-9b51-ae1080cf73d7.jpg" width="320" height="640"> </td>
<td> <img src="https://user-images.githubusercontent.com/49554106/121327939-16a50180-c93e-11eb-99e4-a71dfcf5592f.jpg" width="320" height="640"/> </td>
</tr></table>

### How to Install
___

Clone this repository and import to Android Studio

> https://github.com/mohnaufalp2000/Bangkit-2021-Capstone-Project-B21-CAP0468.git

Or Download APK and install it from your phone

> https://drive.google.com/drive/folders/1fWlzHEYdglEU8mQT7UaZOC-jZmf4KNL1?usp=sharing

### Resource
___

#### Dataset

1. Fresh and Rotten Fruits -> (https://www.kaggle.com/sriramr/fruits-fresh-and-rotten-for-classification)
2. Leaf -> (https://archive.ics.uci.edu/ml/datasets/leaf)

#### Rest API

1. Fruit -> https://www.fruityvice.com/

### Documentation
___

#### Android Programming

We started from designing a UI/UX design using figma. After that, we created a new project in Android Studio with project name "Fresco". We included many libraries in our project to support our app like firebase library, google ads library, retrofit library, etc. After that, we started to do a layouting with source code from our UI/UX Design Apps. After that, we started to make a logic code for our app which it will make the app run properly. Start from creating an intent for moving an activity to another activity. Next, we created an authorization. Authorization is useful login and sign up feature in our app. We work together with cloud engineer in our team to make an authorization.
  
Last but not least, we started to make image recognition. We made an feature which it can make phone capture an image with camera and pick an image from galery, and save it to the layout. After that we import a custom model tensorflow lite from the machine learning team. We also make a feature for saving the scanned image to the firestore which is handled by cloud engineer.  

#### Cloud Computing

Manage GCP billing accounts, top-up the credit that has been given. After that we assign android team account manage roles for them on GCP and firebase. Create a database in cloud firestore and buckets on cloud storage

#### Machine Learning

For fresh or rotten fruits model we used the dataset from kaggle (https://www.kaggle.com/sriramr/fruits-fresh-and-rotten-for-classification), this dataset contains a total of 6 class which are photos of bananas (rotten and fresh), orange (rotten and fresh) and apple (rotten or fresh). It has 2 directories, train which contains a total number of 10901 photos of the classes, and test directory which contains 2698 photos of the classes. With that said we created a CNNs model using tensorflow and keras library. The model contains three layers of convolutional and max pooling layers and after the 3 layers we applied a flattening layer followed by a dropout layer, after that we applied a dense layer followed by dropout layer and lastly an output layer. We compiled the model using the “adam” optimizer and used “categorical_crossentropy” since the model was classifying 6 classes and trained it with 15 epochs. In this model, we applied only one form of data augmentation techniques, we applied only pixel scaling. In the end, the model has achieved quite high accuracy both on the training and validation set (acc: 0.9922, val_acc: 0.9789), we believe the model could still improve with the help of broader dataset, further data augmentation techniques and or applying transfer learning techniques for much better results. 
  
For leaf classification model we used the dataset from UCI (https://archive.ics.uci.edu/ml/datasets/leaf), this dataset contains a total of 100 classes of various leaf images, which each class has 16 images, so the dataset has 1600 total images. With that said we created a CNNs model using tensorflow and keras library. The model contains three layers of convolutional and max pooling layers and after the 3 layers we applied a flattening layer followed by a dropout layer, after that we applied a dense layer followed by an output layer. We compiled the model using the “adam” optimizer and used “categorical_crossentropy” since the model was classifying 100 classes and trained it with 50 epochs. In this model, we applied a few forms of data augmentation techniques in the training set, we applied pixel scaling, horizontal flips, zooming in on images and shear ranging. In the end, the model has achieved quite high accuracy both on the training and validation set (acc: 0.9054, val_acc: 0.8333) . We can say there is a little bit of overfitting going on but since the data is limited there isn't much which can be done. We believe the model could still improve with the help of a broader dataset, further data augmentation techniques and or applying transfer learning techniques for much better results. 
  

### Author
___

1. <a href="https://github.com/mohnaufalp2000" target="_blank">Mohammad Naufal Pratama A2962680</a>
2. <a href="https://github.com/MAbyanN" target="_blank">Muhammad Abyan Naufal C3122791</a>
3. <a href="https://github.com/Astudent35" target="_blank">Muhammad Haider Aslam M0080852</a>
4. <a href="https://github.com/paskalhensa" target="_blank">Paskalis Henry Satritama M0080847</a>
5. <a href="https://github.com/dedyramadhan" target="_blank">Dedy Ramadhan A2962675</a>
