package rewards.messaging;

import org.springframework.mail.MailMessage;
import rewards.RewardConfirmation;

/**
 * @author Dominic North
 */
public interface RewardMailCreator {
    /**
     * Create e-mail message for confirmation
     *
     * @param confirmation
     * @return mail message
     */
    MailMessage createMail(RewardConfirmation confirmation);
}
