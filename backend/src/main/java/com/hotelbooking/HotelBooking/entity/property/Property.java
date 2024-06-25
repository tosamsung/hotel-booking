package com.hotelbooking.HotelBooking.entity.property;

import java.util.Date;

import com.hotelbooking.HotelBooking.entity.business.BusinessAccount;
import com.hotelbooking.HotelBooking.entity.location.City;
import com.hotelbooking.HotelBooking.enums.PropertyStatus;
import com.hotelbooking.HotelBooking.enums.PropertyType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Properties")
public class Property {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private String image;
	
    @Enumerated(EnumType.STRING)
	private PropertyType propertyType;
    
    @Enumerated(EnumType.STRING)
	private PropertyStatus propertyStatus;
	private Date createDate;
	private String address;
	@ManyToOne
	@JoinColumn(name = "city_id", referencedColumnName = "cityName", nullable = false)
	private City city;
	@ManyToOne
	@JoinColumn(name = "business_account_id", referencedColumnName = "id", nullable = false)
	private BusinessAccount businessAccount;
}
