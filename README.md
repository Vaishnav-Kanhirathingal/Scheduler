# Scheduler

Scheduler is an application which helps in scheduling tasks for you so that you don't have to
remember them. Scheduler creates un-dismissible notification which you can remove by pressing a
dismiss button. The app also allows you to postpone the task by up to 5 days

## Reminder notifications and how it works (in notifications)

The app creates a notification at the start of every day or at the creation of a task which is
scheduled for the day itself. Each task notification has two buttons. One being the dismiss button
and the other being a postpone button. The postpone button postpones the task by a few days. How
many days a task gets postponed depends on the postpone value you enter at the creation of the task.

1. The first GIF is a representation of how a task gets added on creation if it is scheduled for the
   same day.
2. The second image displays how two tasks can have different postpone durations.
3. The third image shows a task with long description

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/5c15cf3a-a3e1-4f8c-9d95-d9a811e47e77" width=273>
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/2b81e4a0-d21c-4d84-8698-04b4e01f9a90" width=273>
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/8a0b3ec7-24bd-4308-9064-a158bb98301f" width=273>

## Why

An issue with keeping reminders is you have to go into the apps or maybe a notepad to check what
tasks are scheduled for today. This increases the chances of you forgetting the existence of such a
list of task. With this app, you can schedule a task for a specific day and the app would create an
un-dismissible which the user can dismiss if the tasks gets completed or can have it postponed if he
wants to. This means that the notification would be present till either the user dismisses it or
postpones it for another day.

## Releases

App releases or APKs can be found in
the [release](https://github.com/Vaishnav-Kanhirathingal/Scheduler/releases) section.

## Documentation

This section contains the documentation for the Scheduler app.

### Signing up (Sign up screen)

The app uses google sign in to create an account. Press the button to open sign up prompt. Select
the account of choice and continue. All tasks are stored in the cloud.

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/62bce314-7487-4fc1-967e-5be56a7f7f1e" width=410>
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/1c554c5d-246c-4706-9a18-8b018bc20aac" width=410>

### Empty main screen (main screen)

This is the main screen of the app. Currently there are no tasks. So, we can now move to create a
task. To add a task, click on the `add task` button

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/20c1dd30-39c2-496e-aa01-df276dac6db7" width=360>

### Adding a task (add task screen)

To add a task, you have to fill out the necessary details. An example for creating a sample task has
been demonstrated below. From images 2 and 3, we can verify that the task title and description
should have character length of more than 5 and 10 respectively. image 5 shows how to add reminder
time and image 6 shows how to add task date. Image 7 shows how a task can be repeatable. A task can
either repeat after a certain interval or on a specific day of every month (the day of month for
date-wise reminders can't be more than 28 since that would cause an exception for february).
Postpone duration in image 8 is how much the task can be postponed. And finally, we add the task.

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/b5cd7d7b-d5db-4a5f-a5ef-ebef97fa438c" width=273 title="1">
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/281423d6-7b7e-4f07-9e94-f7246146f019" width=273 title="2">
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/73870797-c8e5-493d-b63a-db287715fb3a" width=273 title="3">
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/643c650c-2fe7-4dd7-8aac-7e0f068e7a12" width=273 title="4">
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/27ac88bd-2b00-463e-9d14-8bcd98305985" width=273 title="5">
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/a7ff2460-3b25-4ca4-a487-0f1da4914280" width=273 title="6">
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/85598ebf-e5c7-497e-8b0d-12be63dfda7a" width=273 title="7">
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/181be8c3-6a61-47fa-8fca-278db0f09ff3" width=273 title="8">
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/16d3e5fb-786a-4f95-a3b1-71e22f06d0a3" width=273 title="9">

### filters on the main screen

filters are of four types

1. All - Displays all Tasks
2. Day - Displays tasks scheduled for the day
3. Week - Displays tasks scheduled within 7 days
4. Month - Displays tasks scheduled within 30 days

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/42861ea4-20cf-4bd7-b65e-b997a40b754c" width=410>
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/1bbdf3c8-52cb-4db8-80ce-ae3d45caa214" width=410>
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/f0dd45fc-2304-4892-a4cc-7196845bac06" width=410>
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/6fca261e-58f1-4248-ade7-b41e34ce3a58" width=410>

### deleting task from main screen

to delete a task from the main screen, you can press the delete icon button on the top right of
every task card. Pressing the button brings up a prompt to delete the task from the database.

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/26e9f1e8-8df6-45e7-8c14-4517c08f8209" width=360>

### side menu (main screen's navigation drawer)

the side menu contains the user's profile image with their name and email. Below that, A list of all
tasks scheduled for the day are displayed. The drawer menu contains more options to explore.

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/4d139e3c-c85a-489d-9189-2432fb9064b0" width=273>
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/23947ac5-c66e-4b42-807f-c5cf93b1a5f5" width=273>
<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/857b2b60-a2d1-4cf6-bb73-0bc896fbebe5" width=273>

### Settings (in side menu)

------------------------------------------------------------------------------------------------todo

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/4232f063-f5c3-4ec0-a289-5224c02c9b8f" width=360>

![SVID_20230628_144146_1_AdobeExpress](https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/a7048116-0d90-4397-a74e-cb55dd3f7ee5)
![SVID_20230628_144227_1_AdobeExpress](https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/9e1fb16d-51c7-4202-b9d1-98bb2339a771)


### App info (in side menu)

pressing the app info opens a page with two cards. First being the app's description along with it's
Git-Hub link button. The second being the developer card. This contains my account links. Every icon
button is a button to my account on the corresponding account.

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/ff5e66f1-f514-4646-a6ca-34b64fea9384" width=360>

### Documentation (in side menu)

Opens the Git-Hub README.md file which contains the app documentation.

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/d6804551-6c9f-4ac4-adb4-a05dfa3aee72" width=360>

### Git-Hub releases (in side menu)

Opens the release section of the Git-Hub repository.

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/bedf1e0d-48e5-406d-83d7-fcd633a36a90" width=360>

### Exit (in side menu)

Exit button exits the app without closing background tasks

<img src = "https://github.com/Vaishnav-Kanhirathingal/Scheduler/assets/94210466/8d329b99-0415-4c31-81d1-46df9c337f82" width=360>
