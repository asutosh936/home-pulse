function fn() {
    var env = karate.env; // read system property 'karate.env'
    if (!env) env = 'local';

    var config = {
        baseUrl: karate.properties['karate.baseUrl'] || 'http://localhost:8080/home-pulse',
        chatEndpoint: '/api/chat'
    };

    karate.log('Karate env:', env);
    karate.log('Base URL:', config.baseUrl);

    karate.configure('connectTimeout', 10000);
    karate.configure('readTimeout', 30000);

    return config;
}
