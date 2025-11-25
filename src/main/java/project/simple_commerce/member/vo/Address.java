package project.simple_commerce.member.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address(String city, String zipcode) {
}
