CREATE database mydb;
create table mydb.tasks
   (id int not null unique auto_increment,
      task varchar(255),
      primary key (id)
   );

