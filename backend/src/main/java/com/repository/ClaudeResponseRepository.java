public interface ClaudeResponseRepository extends JpaRepository<ClaudeResponse, Long> {
    List<ClaudeResponse> findByUserIdOrderByCreatedAtDesc(Long userId);
}
