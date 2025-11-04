package project.simple_commerce.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.simple_commerce.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
