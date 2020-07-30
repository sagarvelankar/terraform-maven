package tf.s3


import com.deliveredtechnologies.terraform.api.TerraformInit
import com.deliveredtechnologies.terraform.api.TerraformOutput
import com.deliveredtechnologies.terraform.api.TerraformPlan
import com.deliveredtechnologies.terraform.api.TerraformShow
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

class S3SentinelSpec extends Specification {

    @Shared Map tfplan;
    @Shared tags = ["environment2"]

    def setupSpec() {
        Properties tfProperties = new Properties()

        String region = 'us-east-1'
        String environment = 'dev'
        String stackName = 's3'

        TerraformInit init = new TerraformInit(stackName)
        TerraformPlan plan = new TerraformPlan(stackName)

        tfProperties.put('tfRootDir', stackName)
        def tfvars = [region: region, environment: environment]
        tfProperties.put('tfVars', tfvars)
        tfProperties.put(TerraformPlan.Option.planOutputFile.getPropertyName(), "terraform.plan")
        tfProperties.put(TerraformShow.Option.path.getPropertyName(), "terraform.plan")

        init.execute(tfProperties)
        plan.execute(tfProperties)
        tfplan = getTerraformShow(stackName, tfProperties)
    }

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
    def "S3 module provisions a bucket in AWS"() {

        expect:
         //hasDefaultTags(tfplan.resource_changes.findAll({it.type == "aws_s3_bucket"}))
        verifyAllTags(tfplan.resource_changes.findAll({it.type == "aws_s3_bucket"}))

    }

    void verifyAllTags(Collection resourcechanges) {
        verifyAll {
            resourcechanges.each {
                Map tg = it.change.after.tags
                assert tg.containsKey("environment")
                assert tg.containsKey("environment2")
            }
        }

    }
    void hasDefaultTags(def resourceChanges) {
        tags.each {tag ->
            resourceChanges.each {
                def resourceTags = it.change.after.tags
                assert resourceTags.containsKey(tag)
            }

        }

    }


    private def getTerraformShow(String stackName, Properties properties) {
        TerraformShow show = new TerraformShow(stackName)
        String tfShow = show.execute(properties)
        JsonSlurper slurper = new JsonSlurper()
        slurper.parseText(tfShow)
    }

    private def getTerraformOutput(String stackName) {
        TerraformOutput output = new TerraformOutput(stackName)
        String tfOutput = output.execute(new Properties())
        JsonSlurper slurper = new JsonSlurper()
        slurper.parseText(tfOutput)
    }
}
