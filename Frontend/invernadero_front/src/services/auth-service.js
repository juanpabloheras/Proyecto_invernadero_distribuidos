import { onAuthStateChanged  } from "firebase/auth";
import { auth } from "./firebase.js";

export function getCurrentUser() {
    return new Promise((resolve, reject) => {
      const unsubscribe = onAuthStateChanged(
        auth,
        user => {
            unsubscribe();
            resolve(user);
        },
        reject
      )
    })
}