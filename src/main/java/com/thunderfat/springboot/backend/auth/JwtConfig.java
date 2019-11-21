package com.thunderfat.springboot.backend.auth;

public class JwtConfig {
 
	public static final String  RSA_PRIVADA="-----BEGIN RSA PRIVATE KEY-----\r\n" + 
			"MIIEpAIBAAKCAQEA0gs5K84BInHyfE+8HZInMkvTC5tYAVk3Za/oPQH7aITDyQeb\r\n" + 
			"JSjHz9LXQqZkyMuqWdzXkfYE33/j4G6wDO72FbSTdFujGS6RrUa6VTT2NuYBd2Qh\r\n" + 
			"Ytltrpe5peP9/3d9xlkiWbSm6rJDJrHZo7ZlJCQXgHjSGgQeXpl2n5Fctoza93hs\r\n" + 
			"3jyiL6UuWRnd/1sl08wlW/Fp0JMJFihiLWulWaKg52gxm0Gvucxbmmw7QO1vXWSq\r\n" + 
			"1tu+6YxAPbfHuVJF7lOGalf4f68AOWoa1LppUGptqV9lFGsTwE2UkOmGmU9UYuxB\r\n" + 
			"POBj6Uslf8+qZRc/Ki+o27TfB9uM8Dc83qShVwIDAQABAoIBAGT2fB6wQR9bgmF6\r\n" + 
			"ukPEkw68H8/+dckAJ367lXht0XkmCC5Dd7He7lzZTht9fAiAzXuwifZhOWptQQ4K\r\n" + 
			"Zplw69z3FcHion1iQyf1TW/Tq14sJRxMhzfKp8w6pkQ1WUxJFa4X/kj8axd5ZInf\r\n" + 
			"B2yIKgm7R4i7itJylF6IidqdtuEP6NCH4UyOBZ+IHYZfYQ9jvF9Z0qPDVAjlJeBL\r\n" + 
			"N0+Iy9Wihqxpnh6A9tHE4lyhgWq5m2Oz3g2yWmorNyO+sn38/HnlTveuhdZpkbB2\r\n" + 
			"J3/A9UWLsHO6uW8QqoIgGleaQ0D93bYk0XFmoTsqcGCdIYllE6yPNn/1GWoS3diI\r\n" + 
			"Zimz0rECgYEA7yNarMyIqNKnQ4+IzkYBH3p3dFlRMP0IHI5hxPTTzDs3q5jO5ahv\r\n" + 
			"6FzZAvjl5Ycc0B4uP8cg3XxDjXi1FEGTnfK7NjwguYjjitx9yd0xFWRZqvf6W75i\r\n" + 
			"K/UFQKBqnlQ3NXmw35tFP/GHCr/H6ghhnW+eouGBzBLPgADHzXd0O2UCgYEA4Nqx\r\n" + 
			"aNo0a2nqQj2Ody8ZA9ZQdzwD5yMj00chCE45lhJrksYrV1tqH38ugX6pzGL7waTM\r\n" + 
			"qY66JfYWg86Z4klxwoAbXq+1A9xbJltyA2R1vBVGbX/xlRjG0xCtd1MQm6CKVzYO\r\n" + 
			"Jx5xuQ7EQSWyRqfoJ0g4SjrsBXUu1RyAsKyOhAsCgYEA7LvFEfdQ+j+/keRP/FPU\r\n" + 
			"3MKx6p5Y//QdyZcyNnZgDXg5/kfB2hxK3G697nFTtUtyPJ0ED+MjJ9/SSATMzga1\r\n" + 
			"CdG3f1M62jEngNoSNHoiExlKozPlxljMetOOpZGouqCIHwofDN17SnfhxcRhNc+i\r\n" + 
			"Quay1nADvHCKth8PrNmqWPkCgYA+msVAg1iK+59cuBTdfECDv/0BZbVUeoU0Ax7g\r\n" + 
			"WB9SeGh5IjHZDbu7b0a8iU6veHPOE5tkLjgH77+PDUvtJDPHSrHUKCnHHGhIgrd4\r\n" + 
			"2FSrfGTygef6cj6Rb5hMm9UI76b05zkDjUdulfzwgpVjdMZ/gW0ixcsHLfxPG7N8\r\n" + 
			"/V5kxwKBgQCawm7LDdWj0pUfR2Aca9NlK9L2Xpv70nWS21/wwphamldYJgJYMsT0\r\n" + 
			"IfVidUJXGcNDViUmKOi1ng/k5GnY/nP2gV+/M/73Mk39X7Wnf96mRJ5aeWWGeF4c\r\n" + 
			"vEqPcJdMjRvG018bzsbjT1AqnMH2sLcX2alk6DkE+uaJtcvD7muwaQ==\r\n" + 
			"-----END RSA PRIVATE KEY-----";
	
	public static final String RSA_PUBLICA= "-----BEGIN PUBLIC KEY-----\r\n" + 
			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0gs5K84BInHyfE+8HZIn\r\n" + 
			"MkvTC5tYAVk3Za/oPQH7aITDyQebJSjHz9LXQqZkyMuqWdzXkfYE33/j4G6wDO72\r\n" + 
			"FbSTdFujGS6RrUa6VTT2NuYBd2QhYtltrpe5peP9/3d9xlkiWbSm6rJDJrHZo7Zl\r\n" + 
			"JCQXgHjSGgQeXpl2n5Fctoza93hs3jyiL6UuWRnd/1sl08wlW/Fp0JMJFihiLWul\r\n" + 
			"WaKg52gxm0Gvucxbmmw7QO1vXWSq1tu+6YxAPbfHuVJF7lOGalf4f68AOWoa1Lpp\r\n" + 
			"UGptqV9lFGsTwE2UkOmGmU9UYuxBPOBj6Uslf8+qZRc/Ki+o27TfB9uM8Dc83qSh\r\n" + 
			"VwIDAQAB\r\n" + 
			"-----END PUBLIC KEY-----";
}
