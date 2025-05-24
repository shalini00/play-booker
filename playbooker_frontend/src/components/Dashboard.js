import React from 'react'
import CustomButton from './CustomButton'
import { isUserLoggedIn } from '../utils/tokenUtil';

const Dashboard = () => {

  return (
    <div style={{display: 'flex', justifyContent: 'space-between'}}>
        <div>Dashboard</div>
        <CustomButton buttonName={isUserLoggedIn() ? "Logout" : "Login"} />
    </div>
  )
}

export default Dashboard