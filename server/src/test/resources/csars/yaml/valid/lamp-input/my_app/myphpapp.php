<?php
// include the credentials to connect to the db
include_once "mysql-credentials.php";

// get task from post after task was entered in form
$post = $_POST['task'];
if ($post != "") {
    saveToDb($post);
}
/**
* saved task to db
*/
function saveToDb($task)
{
    $conn = newDbConnection();
    $task = htmlspecialchars($task);
    if (!$conn->query("INSERT INTO tasks(task) VALUES('".$task."')")) {
        echo("Creating task failed");
    }
    $conn->close();
}
/**
* reads from db and prints it in html
*/
function readFromDb()
{
    $sql = "select * from tasks";
    $conn = newDbConnection();
    $result = $conn->query($sql);
    $conn->close();
    if ($result->num_rows > 0) {
    // output data for each row
    while ($row = $result->fetch_assoc()) {
        echo htmlspecialchars("id: " . $row['id']. " - Task: " . $row['task'])."<br>";
    }
    return;
    }
    echo "0 results";
}
/**
* generates new DB connection with given credentials
*/
function newDbConnection()
{
    extract($GLOBALS);
   $conn = new mysqli($db_host, $db_user, $db_password, $db_name, $db_port);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    return $conn;
}
?>
<!DOCTYPE html>
<html>
   <head>
      <meta charset="utf-8">
      <title>SimpleTaskApp</title>
      <style>
      body {
         font-family: sans-serif;
      }
      </style>
   </head>
   <body>
      <h1>SimpleTaskApp</h1>
      <!-- form to enter tasks -->
      <form class="insertTask" action="myphpapp.php" method="post">
         <input type="text" name="task" />
         <button type="submit" name="button">submit</button>
      </form>
      <?php
      //print tasks out of the db
      readFromDb();
       ?>
   </body>
</html>
