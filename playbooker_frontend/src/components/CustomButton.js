import React from 'react'
import { BASE_URL, isUserLoggedIn } from '../utils/tokenUtil';
import { useNavigate } from 'react-router-dom';

const CustomButton = ({buttonName}) => {
  const navigate = useNavigate();

  const buttonHandler = async () => {
    if (isUserLoggedIn()) {
      const refreshToken = localStorage.getItem('refresh_token')
      await fetch(`${BASE_URL}/users/logout?refreshToken=${atob(refreshToken)}`, {
        method: 'POST'
      });
      localStorage.removeItem('refresh_token');
      localStorage.removeItem('access_token');
    }
    navigate("/login");
  }
  return (
    <div 
        style={{padding: '8px', margin: '20px', borderRadius: '10px', color: 'white', backgroundColor: '#008fd3', cursor: 'pointer'}}
        onClick={buttonHandler}
      >
        {buttonName}
      </div>
  )
}

export default CustomButton