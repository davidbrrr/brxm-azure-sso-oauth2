<%@ page import="java.util.*" %>
<script type="application/javascript">
    function redirect() {
        let url = '<%= request.getAttribute("logoutUrl") %>';
        if (window.location !== window.parent.location) {
            window.parent.location.assign(url);
        } else {
            window.location.assign(url);
        }
        return false;
    }

    redirect();
</script>
