import React, { useState } from 'react';
import './BookingForm.css';
import CustomButton from './CustomButton';

const BookingForm = () => {
  const [formData, setFormData] = useState({
    playSpaceId: '',
    startTime: '',
    endTime: '',
    bookingDate: '',
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const proceedForBooking = async (bookingData) => {
    try {
      const response = await fetch("http://localhost:8080/api/v1/booking/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(bookingData),
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error("Login failed");
      }
      return await response.json();     
    } catch (error) {
      alert(error.message);
    }
  }

  const makePayment = async (order) => {
    const options = {
        "key": "rzp_test_lo5nk3ZMxjAgYH",
        "amount": order.totalPrice,
        "currency": "INR",
        "name": "Play Booker",
        "description": "",
        "order_id": order.paymentId,
        "callback_url": "http://localhost:8080/api/v1/booking/payment-callback",
        "prefill": {
            "email": order.email,
        }
    };
    const rzp1 = new window.Razorpay(options);
    rzp1.open();
}

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    // Add :00 seconds if not present
    const formatWithSeconds = (datetime) =>
      datetime.length === 16 ? `${datetime}:00` : datetime;
  
    const formattedData = {
      ...formData,
      startTime: formatWithSeconds(formData.startTime),
      endTime: formatWithSeconds(formData.endTime),
    };
  
    console.log('Booking Data:', formattedData);

    const bookingRequestData = await proceedForBooking(formattedData);
    console.log(bookingRequestData, 69)
    makePayment(bookingRequestData);
  };
  

  return (
    <>
    <div style={{display: 'flex', justifyContent: 'space-between'}}>
      <div>Book You Playspace</div>
      <CustomButton buttonName="Logout" />
    </div>
      <form onSubmit={handleSubmit} className="booking-form">
      <div className="form-group">
        <label>Playspace ID</label>
        <input
          type="text"
          name="playSpaceId"
          value={formData.playSpaceId}
          onChange={handleChange}
          required
        />
      </div>
      <div className="form-group">
        <label>Start Time</label>
        <input
          type="datetime-local"
          name="startTime"
          value={formData.startTime}
          onChange={handleChange}
          required
        />
      </div>
      <div className="form-group">
        <label>End Time</label>
        <input
          type="datetime-local"
          name="endTime"
          value={formData.endTime}
          onChange={handleChange}
          required
        />
      </div>
      <div className="form-group">
        <label>Booking Date</label>
        <input
          type="date"
          name="bookingDate"
          value={formData.bookingDate}
          onChange={handleChange}
          required
        />
      </div>
      <button type="submit" className="submit-button">Book Now</button>
      </form>

      
    </>
  );
};

export default BookingForm;
