# 🦆 **Quackstagram**

Quackstagram is a lightweight **social media platform** that allows users to **connect through image sharing, likes, and comments**.  
Designed with a **simple and intuitive UI**, it stores data in text files and includes **moderation tools** for safety.  
Users can **explore content, engage with posts, and follow others** for a fun and interactive experience. 

---

## **📌 Project Overview**

Development of this project was divided into two key phases:

1️⃣ **Requirement Analysis & Refactoring**  
2️⃣ **Design Patterns Implementation & New Features**

---

## **📖 How It Works**

### 🔐 **Signing Up & Logging In**
✅ **New users** create an account by providing a **username, password, and profile details**.  
✅ **Returning users** log in and access their **personalized feed**.

### 🖼️ **Posting Images**
📌 Users can **upload images** with captions and **optional tags**.  
📌 The system **supports multiple image formats** and ensures proper display.

### 🌍 **Browsing Content (Explore Page)**
📌 **Home Feed** displays posts from **followed users**.  
📌 **Explore Page** helps users discover **trending and new content** from other students.

### ❤️ **Interacting with Posts**
💬 Users can **like posts** and view the total **number of likes**.  
💬 They can **leave comments**, which are visible to others.

### 👥 **Following Other Users**
🔗 Users can **follow/unfollow** others.  
🔗 Posts from followed users appear in the **Home Feed**.

---

## **🛠️ Technical Flow of the Application**

### 1️⃣ **User Authentication**
✔ When a user logs in, their credentials are **verified** using data stored in **text files**.  
✔ If credentials **match**, access is granted.

### 2️⃣ **Data Storage & Retrieval**
✔ All user profiles, images, likes, and comments are stored in **structured text files**.  
✔ The system **reads and writes** data dynamically upon user actions.

### 3️⃣ **GUI Navigation**
✔ The **user interface** consists of various screens (**Swing panels**) for:
- Login
-  Profile
- Home Feed
- Explore Page
- Notifications 
- 
  ✔ **Event-driven interactions** are triggered upon button clicks.

### 4️⃣ **Post Upload & Display**
✔ Uploaded images are stored as **file references**.  
✔ The application dynamically **loads and displays** images.

### 5️⃣ **User Interaction Handling**
✔ **Likes and comments** are recorded and updated in **text files**.  
✔ **Notifications** are triggered based on user interactions.

---

## **🚀 Example User Journey**

🔹 **Marikie**, a new user, signs up for Quackstagram.  
🔹 She uploads a **profile picture and bio**.  
🔹 She browses the **Explore Page** and finds an **interesting post**.  
🔹 She **likes** the post and **follows** the user.  
🔹 She **uploads her own photo** with a funny caption.  
🔹 A **friend comments** on her post. She gets a **notification**.  
🔹 An **admin removes an inappropriate comment** on another post.

✅ Marikie enjoys interacting on Quackstagram!

---

## **📥 Getting Started**

### 🔧 **Installation**
To clone the project from GitHub, use:
```bash
git clone https://github.com/sgrisshk/Project-OOM.git
```
### 💻 **Running**
To run this code insert these commands into your terminal:
```bash
javac SignInUI.java
```
```bash
java SignInUI
```