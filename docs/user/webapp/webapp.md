# WebApp

Welcome to the user guide for the TOSCAna WebApp. If you are using TOSCAna like descriped in [Getting Started](../getting-started.md) the WebApp application is already started and ready to use.


## The start page
![](img/start_page.png)
*The start page without any csars*

This page shows the default state of the app if no csars were added previously.

## The top bar
![](img/topbar.png)
*The topbar*

**Status:** The status shows the current state of the TOSCAna transformer, idling means he has nothing to do. If he is processing a transformation the status is _TRANSFORMING_.

**Disk Space:** Disk space show how much server disk space is left.

## CSARs

### How to add a CSAR
**1. Step:** Click on the ![](img/add_csar_button.png) button. After clicking on the button the app should look like in the image below.
![](img/add_new_csar_bad.png)

**2. Step:** Enter a name. If you leave the name empty and directly go to **Step 3** the name is automaticly filled in. If the name is a duplicate or something else is wrong the input field and the display indicator will be red. Hovering over the indicator will show the reason why the name is wrong.

**3. Step:** Click on the **Select file** button to upload a csar.

**4. Step:** Submit the csar.

### How to view a CSAR

To view a csar click on it in the sidbar. After clicking the web app should lock similiar to the web app in the image below. Also you can notice that the selected csar is highlighted blue in the sidebar.
![](img/view_csars.png)
*CSAR view for the lamp-input csar*

**Phases:** The web app shows you for each csar processing phase its status. 
**Log:** If something went wrong you might want to check the server logs in the lower part of the web app. With the *log level selector* you can filter unwanted log levels.

### How to delete a csar

To delete a csar click on the trashbin next to the csar name in the sidebar.

![](img/delete_csar.png)  
*To delete for example the dockerapp_missing csar just click on the trashbin on the right side*

## Transformations

### How to add a transformation

**Step 1:** Click on the `+` at csar which you want to create a transformation for

![](img/add_transformation_for_csar.png)  
*Click on the `+` to create a transformation for the lamp input csar*

**Step 2:** Select a platform from the list shown below

![](img/add_transformation_select_platform.png)  
*Pick a platform*

If a transformation for this platform already exists the web app will ask you if you want to overwrite it.

**Step 3:** Enter the required inputs
![](img/add_transformation_enter_inputs.png)  
*Enter the inputs the web app asks you for*

The web asks your for inputs TOSCAna needs to transform your csar. If a input field is highlighted red it means that an inputs is required or that the inputs is wrong. To get more information about an specific input you can hover over the info icon.

**Important:** To get more information about the inputs check the documentation of the platform you want to transform to

**Step 4**: Click on the *Transform* button  
*If you can not click the button something in **Step 3** went wrong.*

After clicking on the button the transformation view gets visible. To learn more about the transformation view just keep scrolling.


### How to view a transformation

In the sidebar click on the csar and then on the transformation you want to look at. After doing this the web app should look similiar to the image below. Also notice the selected transformation is highlighted orange in the sidebar.

**Phases:** The web app shows each transformation phases state.  
**Log:** If something went wrong you might want to check the server logs in the lower part of the web app. With the *log level selector* you can filter unwanted log levels.

**TODO** ADD IMAGE 

The transformation view looks very similiar to the csar view but additionaly there are two download buttons and the inputs and output tabs.

**Inputs:** If you click on the inputs tab you can see the values you entered for the inputs while you created a information.  
**Outputs:** The outputs tab looks very similiar to the inputs tab but has the difference it shows the outputs defined in the csar. Not every plugin can produce outputs so the values might be empty

**Download artifact:** The *Download artifact* button provides the download of the whole artifact created during the transformation.  
**Download run script:** The *Download run script* button provides a script that downloads the artifact, unzips it and starts the deployment. It can be run like that: `bash run.sh`

### How to delete a transformation

To delete a transformation just click on the trashbin next to the transformation name in the sidebar.

![](img/delete_transformation.png)  
*To delete the Kubernetes transformation for the lamp-input just click on the trashbin on the Kubernetes list item*

