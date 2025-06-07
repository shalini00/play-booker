import React, { useState } from 'react';
import './SignupForm.css'; // Ensure your styles are still applied

const SignupForm = () => {
  const [formData, setFormData] = useState({
    name: '',
    username: '',
    email: '',
    password: '',
    phoneNumber: null,
  });

  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false); // 👈 new state

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = 'Name is required';
    if (!formData.username.trim()) newErrors.username = 'Username is required';
    if (!formData.email.trim()) newErrors.email = 'Email is required';
    if (!formData.password.trim()) newErrors.password = 'Password is required';
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validate();
    setErrors(validationErrors);

    if (Object.keys(validationErrors).length === 0) {
      console.log('Form submitted successfully:', formData);
      // Submit form data

      const resp = await fetch("http://localhost:8080/users/create_user", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
        credentials: 'include'
    })

    if (!resp.ok) {
        throw new Error("Login failed");
    }

    const json = await resp.json();

    console.log(json);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="signup-form">
      <h2>Signup Form</h2>

      {['name', 'username', 'email'].map((field) => (
        <div key={field} className="form-group">
          <label>{field.charAt(0).toUpperCase() + field.slice(1)}:</label>
          <input
            type="text"
            name={field}
            value={formData[field]}
            onChange={handleChange}
          />
          {errors[field] && <p className="error">{errors[field]}</p>}
        </div>
      ))}

      <div className="form-group">
        <label>Password:</label>
        <div className="password-wrapper">
          <input
            type={showPassword ? 'text' : 'password'}
            name="password"
            value={formData.password}
            onChange={handleChange}
          />
          <button
            type="button"
            className="toggle-button"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? 'Hide' : 'Show'}
          </button>
        </div>
        {errors.password && <p className="error">{errors.password}</p>}
      </div>

      <div className="form-group">
        <label>Phone Number (optional):</label>
        <input
          type="tel"
          name="phone"
          value={formData.phoneNumber}
          onChange={handleChange}
        />
      </div>

      <button type="submit">Sign Up</button>
    </form>
  );
};

export default SignupForm;
