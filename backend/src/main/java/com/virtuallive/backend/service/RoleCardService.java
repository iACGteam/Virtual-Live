package com.virtuallive.backend.service;

import com.virtuallive.backend.model.entity.RoleCard;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.repository.RoleCardRepository;
import com.virtuallive.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleCardService {
    
    private final RoleCardRepository roleCardRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<RoleCard> getUserRoleCards(Integer userId) {
        return roleCardRepository.findByUserUserIdOrderBySubmitTimeDesc(userId);
    }
    
    @Transactional
    public RoleCard createRoleCard(Integer userId, RoleCard roleCard) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        roleCard.setUser(user);
        roleCard.setStatus(RoleCard.Status.approved); // Auto approve for now
        
        return roleCardRepository.save(roleCard);
    }
    
    @Transactional
    public RoleCard updateRoleCard(Integer userId, Integer cardId, RoleCard updatedCard) {
        RoleCard card = roleCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("角色卡不存在"));
        
        if (!card.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此角色卡");
        }
        
        card.setName(updatedCard.getName());
        card.setGender(updatedCard.getGender());
        card.setBirthday(updatedCard.getBirthday());
        card.setHeight(updatedCard.getHeight());
        card.setHobby(updatedCard.getHobby());
        card.setPersonalityTags(updatedCard.getPersonalityTags());
        card.setRaceTags(updatedCard.getRaceTags());
        card.setAppearanceTags(updatedCard.getAppearanceTags());
        card.setBackgroundStory(updatedCard.getBackgroundStory());
        if (updatedCard.getPortrait() != null) {
            card.setPortrait(updatedCard.getPortrait());
        }
        
        return roleCardRepository.save(card);
    }
}
