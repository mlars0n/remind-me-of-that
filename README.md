# This is the application that runs the remindmeofthat.com website #


# Local development #

### 1. Create your database ###

* Create a PostgreSQL database
    * As the DB root/admin user:

```shell
postgres=# create database remindme;
CREATE DATABASE
postgres=# create user remindmeuser with encrypted password '<PASSWORD_HERE>';
CREATE ROLE
postgres=# grant all privileges on database remindme to remindmeuser;
GRANT
```

### Notes on the system design and architecture ###

The main reminder engine will work like this: 

There will be a reminder_configuration entity, and then a reminder child entity. 

The scheduled jobs that run will kick off and check backwards for necessary deliveries based on whatever time period they are configured
to look for. All reminders will be set up at the time of configuration. The reminder entity/table will 
function as a record of all reminders sent along with all upcoming reminders.

The scheduled jobs will be configurable to send at any interval for scalability. This will be able to go down
to granularity of one second, such that we could have 60 scheduled jobs running, each handling a reminder with a 
time settings in its own second.

reminder_configuration:
* id
* user_id (fk)
* name 
* body
* recurring
* recurring_period (e.g. every two weeks, etc)
