package com.example.debtmatesbe.service;

import com.example.debtmatesbe.dto.rotational.RotationalPlanRequest;
import com.example.debtmatesbe.model.*;
import com.example.debtmatesbe.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RotationalService {

    @Autowired
    private RotationalGroupRepository groupRepository;

    @Autowired
    private RotationalPlanRepository planRepository;

    @Autowired
    private RotationalPaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public RotationalGroup createGroup(String groupName, String groupDescription, int numMembers, User creator) {
        RotationalGroup group = new RotationalGroup();
        group.setGroupName(groupName);
        group.setGroupDescription(groupDescription);
        group.setNumMembers(numMembers);
        group.setCreator(creator);
        group.getMembers().add(creator); // Creator is automatically a member
        return groupRepository.save(group);
    }

    @Transactional
    public RotationalGroup editGroup(Long groupId, String groupName, String groupDescription, int numMembers, User currentUser) {
        RotationalGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the creator can edit the group");
        }
        group.setGroupName(groupName);
        group.setGroupDescription(groupDescription);
        if (numMembers < group.getMembers().size()) {
            throw new RuntimeException("New member limit cannot be less than current members");
        }
        group.setNumMembers(numMembers);
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long groupId, User currentUser) {
        RotationalGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the creator can delete the group");
        }
        groupRepository.delete(group);
    }

    @Transactional
    public RotationalGroup addMembers(Long groupId, List<Long> memberIds, User currentUser) {
        RotationalGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the creator can add members");
        }
        List<User> newMembers = memberIds.stream()
                .map(id -> userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")))
                .collect(Collectors.toList());
        if (group.getMembers().size() + newMembers.size() > group.getNumMembers()) {
            throw new RuntimeException("Cannot exceed maximum number of members");
        }
        group.getMembers().addAll(newMembers);
        return groupRepository.save(group);
    }

    @Transactional
    public List<RotationalPlan> addPlan(Long groupId, List<RotationalPlanRequest> planRequests, User currentUser) {
        RotationalGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the creator can add a plan");
        }
        if (group.getMembers().size() != group.getNumMembers()) {
            throw new RuntimeException("Group must have all members added before creating a plan");
        }
        List<RotationalPlan> plans = planRequests.stream().map(req -> {
            RotationalPlan plan = new RotationalPlan();
            plan.setGroup(group);
            plan.setMonthNumber(req.getMonthNumber());
            plan.setRecipient(userRepository.findById(req.getRecipientId())
                    .orElseThrow(() -> new RuntimeException("Recipient not found")));
            plan.setAmount(req.getAmount());
            return plan;
        }).collect(Collectors.toList());
        planRepository.saveAll(plans);

        plans.forEach(plan -> {
            List<User> payers = group.getMembers().stream()
                    .filter(m -> !m.getId().equals(plan.getRecipient().getId()))
                    .collect(Collectors.toList());
            List<RotationalPayment> payments = payers.stream().map(payer -> {
                RotationalPayment payment = new RotationalPayment();
                payment.setPlan(plan);
                payment.setPayer(payer);
                payment.setRecipient(plan.getRecipient());
                payment.setAmount(plan.getAmount());
                payment.setStatus("Not Started");
                return payment;
            }).collect(Collectors.toList());
            paymentRepository.saveAll(payments);
        });

        return plans;
    }

    public List<RotationalGroup> getUserGroups(User user) {
        List<RotationalGroup> createdGroups = groupRepository.findByCreator(user);
        List<RotationalGroup> memberGroups = groupRepository.findByMembersContaining(user);
        createdGroups.addAll(memberGroups);
        return createdGroups.stream().distinct().collect(Collectors.toList());
    }

    public List<RotationalPayment> getPayments(Long groupId) {
        RotationalGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        List<RotationalPlan> plans = planRepository.findByGroup(group);
        return plans.stream()
                .flatMap(plan -> paymentRepository.findByPlan(plan).stream())
                .collect(Collectors.toList());
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}