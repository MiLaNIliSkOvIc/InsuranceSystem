# InsuranceSystem  
A system for purchasing insurance  

**InsuranceSystem** is an application designed for purchasing various types of insurance, such as life insurance, property insurance, and more.  
The system is built with a strong focus on security. To access the application, users must possess a digital certificate to communicate with the system.  
The application communicates using the HTTPS protocol. Additionally, the login process includes two-factor authentication (2FA), which consists of a username/password combination and a verification code sent to the user's email.  

---

## Features  
### Login Page with Digital Certificate Authentication  
![Digital Certificate Selection](https://github.com/user-attachments/assets/173ce050-a317-45ef-ada3-3315d70319cb)  
- Users must select a digital certificate for authentication.  

### Login Page with Digital Certificate Authentication  
![Login Page with Certificate Selection](https://github.com/user-attachments/assets/47b7496a-cf0d-42fa-97ea-318d2d9ef975)  
- The login page allows users to select a digital certificate for authentication.  
- Users enter the required credentials to log in.  

![Login Page](https://github.com/user-attachments/assets/e91d33d7-0adc-4d6e-9c04-48997ea1626b)  
- After providing valid credentials, users proceed to the verification code input form.  

---

### Main Page  
The main page features a sidebar with two options:  

#### 1. **Dashboard**  


![Verification Code Page](https://github.com/user-attachments/assets/22616334-c017-4efa-a177-cafc583ccc67)  
- Displays all available insurance options.  
- Clicking the `Details` button provides a description of the selected insurance and the option to purchase it.  

#### Insurance Purchase  
![Insurance Details](https://github.com/user-attachments/assets/79bbb904-de2e-4d6f-89e1-0698f5e23306)  
- Insurance purchases are handled via the Stripe platform.  

- Clicking `Buy` displays a card input field.  
- After entering the card details, users click `Pay`.  

#### Purchase Confirmation  
![Payment Page](https://github.com/user-attachments/assets/c5abb8b5-8d49-4549-a596-6dc9aa5eaa5b)  
- Upon successful payment, users receive confirmation of the purchase and are redirected to the dashboard.  

#### 2. **My Insurance**  
![Purchase Success](https://github.com/user-attachments/assets/2b771079-8f66-42ff-80b5-0c31596696ad)  
  
- Displays all purchased insurance policies.  
- Users can delete purchased policies.
- ![My Insurance](https://github.com/user-attachments/assets/78a2d15c-e41d-47c9-88c7-a6089cf856a5)
- A search feature is available to filter insurance policies by type.  

---

### Registration Page  
![Search Feature](https://github.com/user-attachments/assets/61d1c898-591a-42c1-b38b-824420171a1e)  
  
- The registration process includes three input fields.  
- If any errors occur, the application notifies the user.  

---

## Admin Application  
In addition to the client-side application, there is an admin application for managing users and adding new insurance policies.  
The applications function using **Single Sign-On (SSO)**, so if an admin logs into one, they gain direct access to the other without needing to log in again.  

### Admin Dashboard  
![Admin Dashboard](https://github.com/user-attachments/assets/740cb669-e3b7-44bb-94e4-80562d40298c)  
- The initial screen shows all insurance policies added by the admin.  
- Admins can add new policies by clicking the `Add Insurance` button.  

#### Adding New Insurance  
![User Management](https://github.com/user-attachments/assets/fedad094-04c4-4715-a86b-00342e64fe28)  
- Clicking the `Add Insurance` button opens a form for entering insurance details.  
- Clicking `Add Insurance` adds the new policy to the system.  

### User Management  

![Add Insurance Form](https://github.com/user-attachments/assets/68da970e-734c-4f30-ba1f-3e485ff25030)  
- The sidebar includes a section for managing users.  
  - Admins can verify users or block them if malicious activities are detected.  

---

## Security Features  
The system includes filters to detect malicious activities, such as:  
- Excessively high payment amounts.  
- Multiple failed login attempts using invalid credentials.  
