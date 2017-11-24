# How to finish the kubernetes deployment

**Prerequisites:** Docker installed

## Step 1: Build and push the docker image

Open a terminal in the folder named docker. Now enter the following:
```
docker build -t {app_name} .
```

Docker now build your docker image.

Now you can push your image to the docker registry.

To achieve this register at [https://hub.docker.com/](https://hub.docker.com/).
Then open a terminal and run:
```
docker login
```

Enter the username and password you registered at the docker hub.

After the login tag the image with the following command:
```
docker tag {app_name} username/{app_name}
```
Replace `username` with the username you registered at the docker hub.

You can now push the image to the docker hub with the following command:
```
docker push username/{app_name}
```
Again replace `username` with the username you registered at the docker hub.


## Step 2: Set the correct image name

Open the file `{kubernetes_resource_file}` with a editor of your choice.
Then search for the line: 
```yaml
image: username/{app_name}
```
If you found that line replace the `username` with your docker hub username.


### Your `{kubernetes_resource_file}` is now ready to go.




