import styled from 'styled-components';
import { Button } from '@mui/material';

export const RedButton = styled(Button)`
  && {
    background: linear-gradient(135deg, #ff416c 0%, #ff4b2b 100%);
    color: white;
    margin-left: 4px;
    border-radius: 8px;
    text-transform: none;
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(255, 65, 108, 0.3);
    transition: all 0.3s ease;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(255, 65, 108, 0.5);
    }
  }
`;

export const BlackButton = styled(Button)`
  && {
    background: rgba(20, 20, 20, 0.8);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: white;
    margin-left: 4px;
    border-radius: 8px;
    text-transform: none;
    font-weight: 600;
    transition: all 0.3s ease;
    &:hover {
      background: rgba(40, 40, 40, 0.9);
      transform: translateY(-2px);
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
    }
  }
`;

export const DarkRedButton = styled(Button)`
  && {
    background: linear-gradient(135deg, #c31432 0%, #240b36 100%);
    color: white;
    border-radius: 8px;
    text-transform: none;
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(195, 20, 50, 0.3);
    transition: all 0.3s ease;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(195, 20, 50, 0.5);
    }
  }
`;

export const BlueButton = styled(Button)`
  && {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: #fff;
    border-radius: 8px;
    text-transform: none;
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(79, 172, 254, 0.3);
    transition: all 0.3s ease;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(79, 172, 254, 0.5);
    }
  }
`;

export const PurpleButton = styled(Button)`
  && {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 8px;
    text-transform: none;
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(118, 75, 162, 0.3);
    transition: all 0.3s ease;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(118, 75, 162, 0.5);
    }
  }
`;

export const LightPurpleButton = styled(Button)`
  && {
    background: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%);
    color: #1a1a2e;
    border-radius: 8px;
    text-transform: none;
    font-weight: 700;
    box-shadow: 0 4px 15px rgba(102, 166, 255, 0.3);
    transition: all 0.3s ease;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 166, 255, 0.5);
    }
  }
`;

export const GreenButton = styled(Button)`
  && {
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    color: #fff;
    border-radius: 8px;
    text-transform: none;
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(17, 153, 142, 0.3);
    transition: all 0.3s ease;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(17, 153, 142, 0.5);
    }
  }
`;

export const BrownButton = styled(Button)`
  && {
    background: linear-gradient(135deg, #e65c00 0%, #f9d423 100%);
    color: white;
    border-radius: 8px;
    text-transform: none;
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(230, 92, 0, 0.3);
    transition: all 0.3s ease;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(230, 92, 0, 0.5);
    }
  }
`;

export const IndigoButton = styled(Button)`
  && {
    background: linear-gradient(135deg, #4776E6 0%, #8E54E9 100%);
    color: white;
    border-radius: 8px;
    text-transform: none;
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(71, 118, 230, 0.3);
    transition: all 0.3s ease;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(71, 118, 230, 0.5);
    }
  }
`;
