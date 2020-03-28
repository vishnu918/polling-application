package Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class firebase {
    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public FirebaseUser getUser() {
        return getAuth().getCurrentUser();
    }

    public String getUserId() {
        return getUser().getUid();
    }

    public FirebaseFirestore getDatabase() {
        return FirebaseFirestore.getInstance();
    }

    public CollectionReference getUsersCollection() {
        return getDatabase().collection("Users");
    }

    public CollectionReference getPollsCollection() {
        return getDatabase().collection("Polls");
    }

    public DocumentReference getUserDocument() {
        return getUsersCollection().document(getUserId());
    }

    public void signOut() {
        if (getUser() != null)
            getAuth().signOut();
    }

    public StorageReference getStorageReference() {
        return FirebaseStorage.getInstance().getReference();
    }
}
