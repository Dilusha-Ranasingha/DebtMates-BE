package com.example.debtmatesbe.service;

import com.example.debtmatesbe.dto.debt.DebtResponse;
import com.example.debtmatesbe.dto.debt.RecordDebtRequest;
import com.example.debtmatesbe.dto.gemini.GeminiDebtAssignment;
import com.example.debtmatesbe.dto.gemini.GeminiRequest;
import com.example.debtmatesbe.dto.gemini.GeminiResponse;
import com.example.debtmatesbe.model.DebtDetails;
import com.example.debtmatesbe.model.GroupDetails;
import com.example.debtmatesbe.model.GroupMembers;
import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.DebtDetailsRepository;
import com.example.debtmatesbe.repo.GroupDetailsRepository;
import com.example.debtmatesbe.repo.GroupMembersRepository;
import com.example.debtmatesbe.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DebtService {

    private final DebtDetailsRepository debtDetailsRepository;
    private final GroupDetailsRepository groupDetailsRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private static final String SYSTEM_INSTRUCTION = "You are an AI assistant tasked with calculating debt assignments for a group expense. Your goal is to assign debts for members who underpaid to members who overpaid, ensuring that the full debt amount is assigned to a single overpayer whenever possible. Only split the debt across multiple overpayers if a single overpayer cannot cover the full debt. You will receive a JSON request with the following structure:\n\n{\n  \"totalBill\": <number>,\n  \"contributions\": [\n    { \"memberId\": <number>, \"amount\": <number> },\n    ...\n  ]\n}\n\n### Instructions:\n1. Calculate the expected contribution per member: `totalBill` divided by the number of members (length of the contributions array).\n2. For each member, calculate their balance: `amount` (contributed) minus the expected contribution.\n3. Identify members who underpaid (balance < 0) and members who overpaid (balance > 0). Members with a balance of 0 have no debt and should not be assigned any payments.\n4. Assign debts for members who underpaid to members who overpaid:\n   - For each member who underpaid, assign their full debt to a single overpayer if possible (i.e., if the overpayer’s balance can cover the full debt).\n   - If no single overpayer can cover the full debt, split the debt across multiple overpayers, ensuring the split amounts are as even as possible.\n   - Update the overpayer’s balance after each assignment.\n   - Do not assign debts to members with a balance of 0.\n5. Return a JSON response with the following structure, providing only the `memberId`, `toWhoPay` (as a `memberId`), and `amountToPay` for each member:\n[\n  {\n    \"memberId\": <number>,\n    \"toWhoPay\": <number or null>,\n    \"amountToPay\": <number or null>\n  },\n  ...\n]\n- `toWhoPay` should be the `memberId` of the person to whom the debt is owed, or `null` if the member has no debt.\n- `amountToPay` should be the amount the member owes, or `null` if they have no debt.\n- Do not include `groupId`, `memberName`, `contributed`, or `expected` in the response, as these will be handled by the application backend.\n- Ensure that each member appears exactly once in the response, unless their debt is split across multiple overpayers, in which case they may have multiple entries.\n\n### Example 1:\n**Request:**\n{\n  \"totalBill\": 5000,\n  \"contributions\": [\n    { \"memberId\": 1, \"amount\": 500 },\n    { \"memberId\": 2, \"amount\": 1500 },\n    { \"memberId\": 3, \"amount\": 0 },\n    { \"memberId\": 4, \"amount\": 3000 },\n    { \"memberId\": 5, \"amount\": 0 }\n  ]\n}\n\n**Response:**\n[\n  {\n    \"memberId\": 1,\n    \"toWhoPay\": 2,\n    \"amountToPay\": 500\n  },\n  {\n    \"memberId\": 2,\n    \"toWhoPay\": null,\n    \"amountToPay\": null\n  },\n  {\n    \"memberId\": 3,\n    \"toWhoPay\": 4,\n    \"amountToPay\": 1000\n  },\n  {\n    \"memberId\": 4,\n    \"toWhoPay\": null,\n    \"amountToPay\": null\n  },\n  {\n    \"memberId\": 5,\n    \"toWhoPay\": 4,\n    \"amountToPay\": 1000\n  }\n]\n\n### Example 2:\n**Request:**\n{\n  \"totalBill\": 7000,\n  \"contributions\": [\n    { \"memberId\": 1, \"amount\": 2000 },\n    { \"memberId\": 2, \"amount\": 2000 },\n    { \"memberId\": 3, \"amount\": 0 },\n    { \"memberId\": 4, \"amount\": 0 },\n    { \"memberId\": 5, \"amount\": 0 },\n    { \"memberId\": 6, \"amount\": 2000 },\n    { \"memberId\": 7, \"amount\": 1000 }\n  ]\n}\n\n**Response:**\n[\n  {\n    \"memberId\": 1,\n    \"toWhoPay\": null,\n    \"amountToPay\": null\n  },\n  {\n    \"memberId\": 2,\n    \"toWhoPay\": null,\n    \"amountToPay\": null\n  },\n  {\n    \"memberId\": 3,\n    \"toWhoPay\": 1,\n    \"amountToPay\": 1000\n  },\n  {\n    \"memberId\": 4,\n    \"toWhoPay\": 2,\n    \"amountToPay\": 1000\n  },\n  {\n    \"memberId\": 5,\n    \"toWhoPay\": 6,\n    \"amountToPay\": 1000\n  },\n  {\n    \"memberId\": 6,\n    \"toWhoPay\": null,\n    \"amountToPay\": null\n  },\n  {\n    \"memberId\": 7,\n    \"toWhoPay\": null,\n    \"amountToPay\": null\n  }\n]";

    @Autowired
    public DebtService(DebtDetailsRepository debtDetailsRepository,
                       GroupDetailsRepository groupDetailsRepository,
                       GroupMembersRepository groupMembersRepository,
                       UserRepository userRepository) {
        this.debtDetailsRepository = debtDetailsRepository;
        this.groupDetailsRepository = groupDetailsRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.userRepository = userRepository;
    }

    public void recordDebt(Long groupId, RecordDebtRequest request) {
        // Authenticate and validate the user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Validate group and creator
        GroupDetails group = groupDetailsRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        if (!group.getCreator().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator can record debts");
        }

        // Validate group members
        List<GroupMembers> members = groupMembersRepository.findByGroupGroupId(groupId);
        if (members.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No members in the group");
        }

        // Validate contributions match group members
        List<Long> memberIds = members.stream()
                .map(member -> member.getUser().getId())
                .toList();
        List<Long> contributionMemberIds = request.getContributions().stream()
                .map(RecordDebtRequest.Contribution::getMemberId)
                .toList();
        if (!memberIds.containsAll(contributionMemberIds) || !contributionMemberIds.containsAll(memberIds)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contributions must include all group members");
        }

        // Calculate expected contribution
        int numMembers = request.getContributions().size();
        double expectedPerMember = request.getTotalBill() / numMembers;

        // Call Gemini API to get debt assignments
        List<GeminiDebtAssignment> assignments = calculateDebtAssignmentsWithGemini(request);

        // Create DebtDetails entities
        List<DebtDetails> debtRecords = new ArrayList<>();
        Map<Long, Double> contributions = request.getContributions().stream()
                .collect(Collectors.toMap(
                        RecordDebtRequest.Contribution::getMemberId,
                        RecordDebtRequest.Contribution::getAmount
                ));

        for (Map.Entry<Long, Double> contribution : contributions.entrySet()) {
            Long memberId = contribution.getKey();
            Double contributed = contribution.getValue();

            DebtDetails debt = new DebtDetails();
            debt.setGroup(group);
            debt.setMember(userRepository.findById(memberId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found")));
            debt.setAmountContributed(contributed);
            debt.setAmountExpected(expectedPerMember);

            // Find matching Gemini assignments for this member
            List<GeminiDebtAssignment> memberAssignments = assignments.stream()
                    .filter(a -> a.getMemberId().equals(memberId))
                    .toList();

            if (!memberAssignments.isEmpty()) {
                // For simplicity, take the first assignment (since we expect one entry per member unless split)
                GeminiDebtAssignment assignment = memberAssignments.get(0);
                if (assignment.getToWhoPay() != null) {
                    debt.setToWhoPay(userRepository.findById(assignment.getToWhoPay())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ToWhoPay member not found")));
                    debt.setAmountToPay(assignment.getAmountToPay());
                }
            }

            debtRecords.add(debt);
        }

        // Save to database
        debtDetailsRepository.saveAll(debtRecords);
    }

    public List<DebtResponse> getGroupDebts(Long groupId) {
        // Authenticate and validate the user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Validate group and membership
        GroupDetails group = groupDetailsRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        List<GroupMembers> memberships = groupMembersRepository.findByGroupGroupId(groupId);
        if (memberships.stream().noneMatch(m -> m.getUser().getId().equals(currentUser.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this group");
        }

        // Fetch and map debt records
        List<DebtDetails> debts = debtDetailsRepository.findByGroupGroupId(groupId);
        return debts.stream()
                .map(this::mapToDebtResponse)
                .toList();
    }

    public List<DebtResponse> getUserDebts() {
        // Authenticate the user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Fetch and map debt records
        List<DebtDetails> debts = debtDetailsRepository.findByMember(currentUser);
        return debts.stream()
                .map(this::mapToDebtResponse)
                .toList();
    }

    private DebtResponse mapToDebtResponse(DebtDetails debt) {
        DebtResponse response = new DebtResponse();
        response.setGroupId(debt.getGroup().getGroupId());
        response.setMemberName(debt.getMember().getUsername());
        response.setContributed(debt.getAmountContributed());
        response.setExpected(debt.getAmountExpected());
        response.setToWhoPay(debt.getToWhoPay() != null ? debt.getToWhoPay().getUsername() : null);
        response.setAmountToPay(debt.getAmountToPay());
        return response;
    }

    private List<GeminiDebtAssignment> calculateDebtAssignmentsWithGemini(RecordDebtRequest request) {
        try {
            // Prepare the Gemini request
            GeminiRequest geminiRequest = new GeminiRequest();
            GeminiRequest.Content content = new GeminiRequest.Content();
            GeminiRequest.Content.Part part = new GeminiRequest.Content.Part();
            part.setText(objectMapper.writeValueAsString(request));
            content.setParts(List.of(part));
            geminiRequest.setContents(List.of(content));

            GeminiRequest.SystemInstruction systemInstruction = new GeminiRequest.SystemInstruction();
            GeminiRequest.SystemInstruction.Part systemPart = new GeminiRequest.SystemInstruction.Part();
            systemPart.setText(SYSTEM_INSTRUCTION);
            systemInstruction.setParts(List.of(systemPart));
            geminiRequest.setSystemInstruction(systemInstruction);

            // Set up HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            // Make the API call
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(geminiRequest, headers);
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    geminiApiUrl + "?key=" + geminiApiKey,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );

            // Process the response
            GeminiResponse geminiResponse = response.getBody();
            if (geminiResponse == null || geminiResponse.getCandidates() == null || geminiResponse.getCandidates().isEmpty()) {
                throw new RuntimeException("Invalid response from Gemini API");
            }

            String responseText = geminiResponse.getCandidates().get(0).getContent().getParts().get(0).getText();
            List<GeminiDebtAssignment> assignments = objectMapper.readValue(
                    responseText,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, GeminiDebtAssignment.class)
            );

            return assignments;
        } catch (Exception e) {
            // Fallback to manual calculation if Gemini API fails
            return calculateDebtAssignmentsManually(request);
        }
    }

    private List<GeminiDebtAssignment> calculateDebtAssignmentsManually(RecordDebtRequest request) {
        List<GeminiDebtAssignment> assignments = new ArrayList<>();
        int numMembers = request.getContributions().size();
        double expectedPerMember = request.getTotalBill() / numMembers;

        // Calculate balances
        Map<Long, Double> balances = request.getContributions().stream()
                .collect(Collectors.toMap(
                        RecordDebtRequest.Contribution::getMemberId,
                        contribution -> contribution.getAmount() - expectedPerMember
                ));

        // Process each member
        for (RecordDebtRequest.Contribution contribution : request.getContributions()) {
            Long memberId = contribution.getMemberId();
            double balance = balances.get(memberId);

            GeminiDebtAssignment assignment = new GeminiDebtAssignment();
            assignment.setMemberId(memberId);

            if (balance < 0) {
                // Member owes money, find an overpayer
                double amountOwed = Math.abs(balance);
                for (Map.Entry<Long, Double> entry : balances.entrySet()) {
                    Long otherMemberId = entry.getKey();
                    double otherBalance = entry.getValue();

                    if (!otherMemberId.equals(memberId) && otherBalance > 0) {
                        double amountToPay = Math.min(amountOwed, otherBalance);
                        assignment.setToWhoPay(otherMemberId);
                        assignment.setAmountToPay(amountToPay);

                        // Update balances
                        balances.put(otherMemberId, otherBalance - amountToPay);
                        amountOwed -= amountToPay;
                        break;
                    }
                }
            }

            if (assignment.getToWhoPay() == null) {
                assignment.setToWhoPay(null);
                assignment.setAmountToPay(null);
            }

            assignments.add(assignment);
        }

        return assignments;
    }
}