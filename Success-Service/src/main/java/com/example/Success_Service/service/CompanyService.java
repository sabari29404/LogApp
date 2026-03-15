package com.example.Success_Service.service;

import com.example.Success_Service.entity.CompanyEntity;
import com.example.Success_Service.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public List<CompanyEntity> getCompanyDetails(){
        return companyRepository.findAll();
    }
}
