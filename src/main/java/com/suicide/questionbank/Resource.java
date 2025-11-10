package com.suicide.questionbank;

import java.util.List;

/**
 * Comprehensive Resource class matching the crisis resources JSON structure.
 */
public class Resource {
    private String name;
    private String parentAgency;
    private List<String> categories;
    private String description;
    private Location location;
    private MailingAddress mailingAddress;
    private String hours;
    private Phones phones;
    private Contact contact;
    private ServiceArea serviceArea;
    private Eligibility eligibility;
    private String fees;
    private String applicationProcess;
    private Requirements requirements;
    private List<String> languagesOffered;
    
    // Nested classes for complex structures
    public static class Location {
        private String address1;
        private String address2;
        private String city;
        private String county;
        private String state;
        private String postalCode;
        private String country;
        
        // Getters and setters
        public String getAddress1() { return address1; }
        public void setAddress1(String address1) { this.address1 = address1; }
        
        public String getAddress2() { return address2; }
        public void setAddress2(String address2) { this.address2 = address2; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getCounty() { return county; }
        public void setCounty(String county) { this.county = county; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }
    
    public static class MailingAddress {
        private String address1;
        private String address2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        
        // Getters and setters
        public String getAddress1() { return address1; }
        public void setAddress1(String address1) { this.address1 = address1; }
        
        public String getAddress2() { return address2; }
        public void setAddress2(String address2) { this.address2 = address2; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }
    
    public static class PhoneNumber {
        private String number;
        private String type;
        
        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
    
    public static class Phones {
        private PhoneNumber primary;
        private PhoneNumber secondary;
        private PhoneNumber third;
        private PhoneNumber fourth;
        private PhoneNumber tollFree;
        private PhoneNumber hotline;
        private PhoneNumber business;
        
        // Getters and setters
        public PhoneNumber getPrimary() { return primary; }
        public void setPrimary(PhoneNumber primary) { this.primary = primary; }
        
        public PhoneNumber getSecondary() { return secondary; }
        public void setSecondary(PhoneNumber secondary) { this.secondary = secondary; }
        
        public PhoneNumber getThird() { return third; }
        public void setThird(PhoneNumber third) { this.third = third; }
        
        public PhoneNumber getFourth() { return fourth; }
        public void setFourth(PhoneNumber fourth) { this.fourth = fourth; }
        
        public PhoneNumber getTollFree() { return tollFree; }
        public void setTollFree(PhoneNumber tollFree) { this.tollFree = tollFree; }
        
        public PhoneNumber getHotline() { return hotline; }
        public void setHotline(PhoneNumber hotline) { this.hotline = hotline; }
        
        public PhoneNumber getBusiness() { return business; }
        public void setBusiness(PhoneNumber business) { this.business = business; }
    }
    
    public static class Contact {
        private String email;
        private String website;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
    }
    
    public static class ServiceArea {
        private List<String> areasCovered;
        private List<String> coverageByCounty;
        
        public List<String> getAreasCovered() { return areasCovered; }
        public void setAreasCovered(List<String> areasCovered) { this.areasCovered = areasCovered; }
        
        public List<String> getCoverageByCounty() { return coverageByCounty; }
        public void setCoverageByCounty(List<String> coverageByCounty) { this.coverageByCounty = coverageByCounty; }
    }
    
    public static class Eligibility {
        private String general;
        private Boolean adults;
        private Boolean children;
        private Boolean families;
        private Boolean females;
        private Boolean males;
        private Boolean teens;
        
        // Getters and setters
        public String getGeneral() { return general; }
        public void setGeneral(String general) { this.general = general; }
        
        public Boolean getAdults() { return adults; }
        public void setAdults(Boolean adults) { this.adults = adults; }
        
        public Boolean getChildren() { return children; }
        public void setChildren(Boolean children) { this.children = children; }
        
        public Boolean getFamilies() { return families; }
        public void setFamilies(Boolean families) { this.families = families; }
        
        public Boolean getFemales() { return females; }
        public void setFemales(Boolean females) { this.females = females; }
        
        public Boolean getMales() { return males; }
        public void setMales(Boolean males) { this.males = males; }
        
        public Boolean getTeens() { return teens; }
        public void setTeens(Boolean teens) { this.teens = teens; }
    }
    
    public static class Requirements {
        private String service;
        private String documents;
        
        public String getService() { return service; }
        public void setService(String service) { this.service = service; }
        
        public String getDocuments() { return documents; }
        public void setDocuments(String documents) { this.documents = documents; }
    }
    
    // Main Resource getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getParentAgency() { return parentAgency; }
    public void setParentAgency(String parentAgency) { this.parentAgency = parentAgency; }
    
    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public MailingAddress getMailingAddress() { return mailingAddress; }
    public void setMailingAddress(MailingAddress mailingAddress) { this.mailingAddress = mailingAddress; }
    
    public String getHours() { return hours; }
    public void setHours(String hours) { this.hours = hours; }
    
    public Phones getPhones() { return phones; }
    public void setPhones(Phones phones) { this.phones = phones; }
    
    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }
    
    public ServiceArea getServiceArea() { return serviceArea; }
    public void setServiceArea(ServiceArea serviceArea) { this.serviceArea = serviceArea; }
    
    public Eligibility getEligibility() { return eligibility; }
    public void setEligibility(Eligibility eligibility) { this.eligibility = eligibility; }
    
    public String getFees() { return fees; }
    public void setFees(String fees) { this.fees = fees; }
    
    public String getApplicationProcess() { return applicationProcess; }
    public void setApplicationProcess(String applicationProcess) { this.applicationProcess = applicationProcess; }
    
    public Requirements getRequirements() { return requirements; }
    public void setRequirements(Requirements requirements) { this.requirements = requirements; }
    
    public List<String> getLanguagesOffered() { return languagesOffered; }
    public void setLanguagesOffered(List<String> languagesOffered) { this.languagesOffered = languagesOffered; }
}

