package com.example.scheduler.firebase

import android.util.Log
import com.example.scheduler.data.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

object DatabaseFunctions {
    private val TAG = this::class.java.simpleName

    /** It receives a task and adds it to the Fire-store database at the appropriate location
     * @param task A Task object to be uploaded to firebase
     * @param onSuccessListener a lambda to run after the function has executed successfully
     * @param onFailureListener a lambda to run after the function has failed to execute. takes the
     * exception message as parameter
     */
    fun uploadTaskToFirebase(
        task: Task,
        onSuccessListener: () -> Unit,
        onFailureListener: (error: String) -> Unit
    ) {
        val data = task.toHashMap()
        val database = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        database
            .collection(FirebaseKeys.parentCollectionName)
            .document(email)
            .collection(FirebaseKeys.listOfTaskName)
            .document()
            .set(data)
            .addOnSuccessListener {
                onSuccessListener()
                Log.d(TAG, "added task to database")
            }.addOnFailureListener {
                onFailureListener(it.message ?: "error while adding task to remote database")
                it.printStackTrace()
            }
    }

    /** responsible for creating user directories based on the user email. It creates the directory
     * for where to store new tasks
     * @param onSuccess a lambda to run after the function has executed successfully
     * @param onFailure a lambda to run after the function has failed to execute. takes the
     * exception message as parameter
     */
    fun createUserDirectories(
        onSuccess: () -> Unit,
        onFailure: (issue: String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        db.collection(FirebaseKeys.parentCollectionName)
            .document(email)
            .set(hashMapOf("userName" to email))
            .addOnSuccessListener {
                Log.d(TAG, "createUserDirectories success")
                onSuccess()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onFailure(e.message ?: "failed to create directories in the database")
            }
    }

    /** it takes a list receiver lambda and passes a list of tasks to it after fetching it from the
     * fire-store
     * @param listReceiver a lambda which is run when the list gets received from a network request.
     * The list is passed as the parameter to the lambda
     * @param onFailure a lambda to run after the function has failed to execute. takes the
     * exception message as parameter
     *
     */
    fun getListOfTasksAsDocuments(
        listReceiver: (List<DocumentSnapshot>) -> Unit,
        onFailure: (issue: String) -> Unit
    ) {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseKeys.parentCollectionName)
            .document(email)
            .collection(FirebaseKeys.listOfTaskName)
            .get()
            .addOnSuccessListener {
                listReceiver(it.documents)
            }.addOnFailureListener {
                it.printStackTrace()
                onFailure(it.message ?: "error occurred while querying list of tasks")
            }
    }


    /**Takes a DocumentSnapshot object of a task and deletes it
     * @param taskDoc a DocumentSnapshot object which can be used to delete the task object from
     * fire-store
     * @param onSuccessListener a lambda to run after the function has executed successfully
     * @param onFailureListener a lambda to run after the function has failed to execute. takes the
     * exception message as parameter
     */
    fun deleteTaskDocument(
        taskDoc: DocumentSnapshot,
        onSuccessListener: () -> Unit,
        onFailureListener: (error: String) -> Unit
    ) {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseKeys.parentCollectionName)
            .document(email)
            .collection(FirebaseKeys.listOfTaskName)
            .document(taskDoc.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "deleted task")
                onSuccessListener()
            }.addOnFailureListener {
                it.printStackTrace()
                onFailureListener(it.message ?: "deleteTaskDocument error")
            }
    }

    /** deletes all tasks stored in the current user's document
     * @param notifyUser a lambda which takes a string to be displayed to the user
     * @param dismissLoadingPrompt a lambda which dismisses the active loading animation prompt
     */
    fun deleteAllTasks(
        notifyUser: (String) -> Unit,
        dismissLoadingPrompt: () -> Unit
    ) {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val listCollection = FirebaseFirestore
            .getInstance()
            .collection(FirebaseKeys.parentCollectionName)
            .document(email)
            .collection(FirebaseKeys.listOfTaskName)

        listCollection.get()
            .addOnSuccessListener {
                var counter = 0
                val size = it.documents.size
                if (size == 0) {
                    dismissLoadingPrompt()
                    notifyUser("Deleted all tasks")

                } else {
                    it.documents.forEach { ds ->
                        listCollection.document(ds.id).delete()
                            .addOnSuccessListener {
                                counter++
                                if (counter == size) {
                                    dismissLoadingPrompt()
                                    notifyUser("Deleted all tasks")
                                }
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                                notifyUser("Failed to delete a task. Repeat the process to make a re-attempt")
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                notifyUser("failed to get list of tasks")
            }
    }
}