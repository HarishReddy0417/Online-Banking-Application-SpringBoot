package com.example.bankproject.entities;

public enum AccountType {


    SAVINGS,CURRENT;
    public static boolean isVaild(String acctType)
    {
        AccountType acctTypes[]=AccountType.values();
        for(AccountType acct:acctTypes)
        {
            if(acct.toString().equals(acctTypes))
            {
                return true;
                
            }
            
        }
        return false;
    }
}
