package tf.s3

import com.deliveredtechnologies.terraform.api.TerraformInit
import com.deliveredtechnologies.terraform.api.TerraformPlan
import com.deliveredtechnologies.terraform.api.TerraformShow
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

class S3SentinelSpec extends Specification {

    @Shared Map tfplan;
    @Shared mandatory_tags = [
            "application_id",
            "stack_name",
            "description",
            "termination_date",
            "name",
            "created_by",
            "data_class",
            "environment"
        ]

    /**
     * application_id
     * stack_name
     * description
     * termination_date
     * name
     * created_by
     * data_class
     * environment
     */
    def "S3 resources have all mandatory tags"() {

        given:
            List s3Buckets = tfplan.resource_changes.findAll({it.type == "aws_s3_bucket"})

        expect:
            hasMandatoryTags(s3Buckets)
            isVersioningEnabled(s3Buckets)
    }

    def "S3 versioning enabled == true"() {

        given:
        List s3Buckets = tfplan.resource_changes.findAll({it.type == "aws_s3_bucket"})

        expect:
        isVersioningEnabled(s3Buckets)
    }

    void isVersioningEnabled(List resources) {
        resources.each {
            Map m = it.change.after
            assert m.containsKey("versioning")
            Map versioning = m.versioning[0]
            assert versioning.enabled == true
        }
    }

    void hasMandatoryTags(List resources) {
        mandatory_tags.each {tag ->
            resources.each {
                def resourceTags = it.change.after.tags
                assert resourceTags.containsKey(tag)
            }
        }
    }

    def setupSpec() {
        Properties tfProperties = new Properties()

        String region = 'us-east-1'
        String environment = 'dev'
        String stackName = 's3_sentinel_demo'

        TerraformInit init = new TerraformInit(stackName)
        TerraformPlan plan = new TerraformPlan(stackName)

        tfProperties.put('tfRootDir', stackName)
        def tfvars = []
        tfProperties.put('tfVars', tfvars)
        tfProperties.put(TerraformPlan.Option.planOutputFile.getPropertyName(), "terraform.plan")
        tfProperties.put(TerraformShow.Option.path.getPropertyName(), "terraform.plan")

        init.execute(tfProperties)
        plan.execute(tfProperties)
        tfplan = getTerraformShow(stackName, tfProperties)

    }

    private def getTerraformShow(String stackName, Properties properties) {
        TerraformShow show = new TerraformShow(stackName)
        String tfShow = show.execute(properties)
        JsonSlurper slurper = new JsonSlurper()
        slurper.parseText(tfShow)
    }
}
