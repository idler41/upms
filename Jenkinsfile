#!groovy
pipeline {
    agent none

    parameters {
        choice(name: 'profile', choices: ['dev', 'test', 'prod'], description: '请选择构建环境[开发，测试，线上]')
        //若勾选在pipelie完成后会邮件通知测试人员进行验收
//        booleanParam(name: 'isCommitQA',description: '是否邮件通知测试人员进行人工验收',defaultValue: false )
    }

    options {
        timeout(time: 20, unit: 'MINUTES')
    }
    stages {

        stage('构建Maven项目') {
            agent { label 'agent01' }
            steps {
                sh "mvn clean package -Dmaven.test.skip=true -P ${params.profile}"
            }
        }

        stage('优雅停止docker容器') {
            agent { label 'agent01' }
            steps {
                sh "docker-compose -f docker/compose-${params.profile}-v3.yml stop"
            }
        }

        stage('重新构建镜像和运行容器') {
            agent { label 'agent01' }
            steps {
                sh "docker-compose -f docker/compose-${params.profile}-v3.yml up -d --build"
            }
        }
    }
}