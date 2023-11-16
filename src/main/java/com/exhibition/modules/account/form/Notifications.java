package com.exhibition.modules.account.form;

import lombok.Data;

@Data
public class Notifications {

        private boolean artAllowedByEmail; //작품이 등록 된 것을 이메일로 받을 것인가?

        private boolean artAllowedByWeb; //작품이 등록 된 것을 웹으로 받을 것인가?


}
